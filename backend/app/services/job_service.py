"""Job service: orchestrates the complete processing pipeline."""

from __future__ import annotations

import asyncio
import logging
import uuid
from typing import Any, Dict, Optional

from app.core.constants import JobStatus, ModelArchitecture
from app.core.exceptions import JobNotFoundError
from app.models.job import ProcessingJob
from app.repositories.job_repository import JobRepository
from app.services.ai_inference_service import AIInferenceService
from app.services.firebase_service import FirebaseService
from app.services.result_generator_service import ResultGeneratorService
from app.services.storage_service import StorageService
from app.utils.temp_file_manager import cleanup_job_temp_dir, create_job_temp_dir

logger = logging.getLogger(__name__)


class JobService:
    def __init__(self) -> None:
        self._job_repo = JobRepository()
        self._firebase = FirebaseService()
        self._storage = StorageService()
        self._result_gen = ResultGeneratorService()

    async def create_job(
        self,
        user_id: str,
        storage_path: str,
        modality: str = "panoramic",
        model_architecture: ModelArchitecture = ModelArchitecture.UNET,
        metadata: Optional[Dict[str, Any]] = None,
    ) -> ProcessingJob:
        job_id = str(uuid.uuid4())
        job = ProcessingJob(
            job_id=job_id,
            user_id=user_id,
            storage_path=storage_path,
            modality=modality,
            model_architecture=model_architecture,
            metadata=metadata,
        )
        await self._firebase.create_job(job.to_dict())
        logger.info("Created job %s for user %s", job_id, user_id)
        return job

    async def get_job(self, job_id: str, user_id: str) -> Dict[str, Any]:
        doc = await self._firebase.get_job(job_id)
        if not doc:
            raise JobNotFoundError()
        # Restrict access to job owner
        if doc.get("user_id") != user_id:
            raise JobNotFoundError()
        return doc

    async def cancel_job(self, job_id: str, user_id: str) -> None:
        doc = await self._firebase.get_job(job_id)
        if not doc or doc.get("user_id") != user_id:
            raise JobNotFoundError()
        await self._firebase.update_job_status(job_id, JobStatus.CANCELLED)
        logger.info("Cancelled job %s", job_id)

    async def process_job(self, job_id: str) -> None:
        """Run the complete AI processing pipeline for a job."""
        doc = await self._firebase.get_job(job_id)
        if not doc:
            raise JobNotFoundError()

        job = ProcessingJob.from_dict(doc)
        temp_dir = create_job_temp_dir(job_id)

        try:
            # Update status → processing
            await self._firebase.update_job_status(job_id, JobStatus.PROCESSING, progress=5)

            # Download file from Firebase Storage
            local_file = await self._storage.download_to_temp(job.storage_path)
            await self._firebase.update_job_status(job_id, JobStatus.PROCESSING, progress=20)

            # Run AI inference
            inference_svc = AIInferenceService(
                architecture=job.model_architecture,
            )
            results = await inference_svc.run(
                file_path=local_file,
                job_id=job_id,
                modality=job.modality,
                metadata=job.metadata,
            )
            await self._firebase.update_job_status(job_id, JobStatus.PROCESSING, progress=70)

            # Upload results
            result_urls = await self._result_gen.upload_results(
                job_id=job_id,
                user_id=job.user_id,
                mask_bytes=results["mask_bytes"],
                overlay_bytes=results["overlay_bytes"],
                heatmap_bytes=results["heatmap_bytes"],
                report_bytes=results["report_bytes"],
            )
            await self._firebase.update_job_status(
                job_id,
                JobStatus.COMPLETED,
                progress=100,
                result_urls=result_urls,
            )
            logger.info("Job %s completed successfully", job_id)

        except Exception as exc:
            logger.error("Job %s failed: %s", job_id, exc, exc_info=True)
            await self._firebase.update_job_status(
                job_id,
                JobStatus.FAILED,
                error_message=str(exc),
            )
            raise
        finally:
            cleanup_job_temp_dir(job_id)
