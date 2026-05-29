import os
import pytest
from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)

def test_health_endpoint():
    """
    Validate the strict contract format of the Health Endpoint.
    ApiResponse = { success, message, data }
    """
    response = client.get("/health")
    assert response.status_code == 200
    json_data = response.json()
    assert json_data["success"] is True
    assert "Backend is online" in json_data["message"]
    assert json_data["data"]["status"] == "healthy"

def test_auth_registration():
    """
    Validate mock/development registration contract checks.
    """
    payload = {
        "email": "researcher@lab.org",
        "password": "SecurePassword123!",
        "displayName": "Principal Investigator"
    }
    response = client.post("/auth/register", json=payload)
    assert response.status_code == 200
    json_data = response.json()
    assert json_data["success"] is True
    assert json_data["data"]["email"] == "researcher@lab.org"
    assert json_data["data"]["displayName"] == "Principal Investigator"

def test_auth_login():
    """
    Validate mock/development login DTO contract structure.
    """
    payload = {
        "email": "researcher@lab.org",
        "password": "SecurePassword123!"
    }
    response = client.post("/auth/login", json=payload)
    assert response.status_code == 200
    json_data = response.json()
    assert json_data["success"] is True
    assert json_data["data"]["email"] == "researcher@lab.org"

def test_upload_invalid_type():
    """
    Validate that strict type-checking rejects non-NIfTI extensions for HIPAA security.
    """
    headers = {"Authorization": "Bearer mock-testuser"}
    files = {"file": ("invalid_image.jpg", b"fake binary data", "image/jpeg")}
    response = client.post("/upload", files=files, headers=headers)
    assert response.status_code == 400
    assert "Only .nii or .nii.gz are supported" in response.json()["detail"]

def test_upload_and_processing_flow():
    """
    Validate end-to-end local data lineage, upload hash calculations,
    deterministic job directories, and status mapping mechanics.
    """
    headers = {"Authorization": "Bearer mock-researcher"}
    
    # 1. Upload a valid mock NIfTI file
    dummy_nifti_data = b"NIFTIBINARYHEADERDATA" + b"\x00" * 100
    files = {"file": ("test_scan.nii", dummy_nifti_data, "application/octet-stream")}
    
    upload_response = client.post("/upload", files=files, headers=headers)
    assert upload_response.status_code == 200
    upload_json = upload_response.json()
    assert upload_json["success"] is True
    
    file_url = upload_json["data"]["fileUrl"]
    file_name = upload_json["data"]["fileName"]
    input_sha = upload_json["data"]["sha256"]
    
    assert file_name == "test_scan.nii"
    assert len(input_sha) == 64  # Valid SHA-256 string length

    # 2. Dispatch the process request
    process_payload = {
        "fileUrl": file_url,
        "fileName": file_name,
        "fileSize": len(dummy_nifti_data),
        "cloudProvider": "firebase",
        "userEmail": "researcher@lab.org"
    }
    
    process_response = client.post("/process/start", json=process_payload, headers=headers)
    assert process_response.status_code == 200
    process_json = process_response.json()
    assert process_json["success"] is True
    
    job_id = process_json["data"]["jobId"]
    assert process_json["data"]["status"] == "QUEUED"

    # 3. Verify deterministic directories are established
    job_dir = os.path.join("data", "jobs", job_id)
    assert os.path.exists(os.path.join(job_dir, "input"))
    assert os.path.exists(os.path.join(job_dir, "output"))

    # 4. Check status polling response format
    status_response = client.get(f"/process/status/{job_id}", headers=headers)
    assert status_response.status_code == 200
    status_json = status_response.json()
    assert status_json["success"] is True
    assert status_json["data"]["jobId"] == job_id
