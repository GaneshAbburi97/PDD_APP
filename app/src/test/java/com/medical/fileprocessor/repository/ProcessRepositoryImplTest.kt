package com.medical.fileprocessor.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.medical.fileprocessor.model.ProcessingJob
import com.medical.fileprocessor.model.ProcessingStatus
import com.medical.fileprocessor.network.*
import com.medical.fileprocessor.util.Resource
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.*
import okhttp3.MultipartBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ProcessRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()

    @MockK
    lateinit var apiService: ApiService

    @MockK
    lateinit var firebaseAuth: FirebaseAuth

    @MockK
    lateinit var firebaseUser: FirebaseUser

    @MockK
    lateinit var firestoreJobRepository: FirestoreJobRepository

    private lateinit var repository: ProcessRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        
        every { firebaseAuth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns "test_user_id"
        
        repository = ProcessRepositoryImpl(
            apiService,
            firebaseAuth,
            firestoreJobRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun checkBackendHealth_success_emitsSuccessResource() = runTest {
        // Arrange
        val healthStatus = HealthStatus(status = "healthy", version = "1.1.0-research", firebase = "initialized")
        val apiResponse = ApiResponse(success = true, message = "healthy", data = healthStatus)
        coEvery { apiService.checkHealth() } returns Response.success(apiResponse)

        // Act
        val results = repository.checkBackendHealth().toList()

        // Assert
        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)
        assertEquals(healthStatus, (results[1] as Resource.Success).data)
    }

    @Test
    fun checkBackendHealth_failure_emitsErrorResource() = runTest {
        // Arrange
        coEvery { apiService.checkHealth() } throws Exception("Network connection lost")

        // Act
        val results = repository.checkBackendHealth().toList()

        // Assert
        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Error)
        assertEquals("Network connection lost", (results[1] as Resource.Error).message)
    }

    @Test
    fun uploadFile_success_emitsSuccessResource() = runTest {
        // Arrange
        val filePart = mockk<MultipartBody.Part>()
        val uploadResponse = UploadResponse(fileUrl = "http://10.0.2.2/static/scan.nii", fileName = "scan.nii")
        val apiResponse = ApiResponse(success = true, message = "uploaded", data = uploadResponse)
        coEvery { apiService.uploadFile(filePart) } returns Response.success(apiResponse)

        // Act
        val results = repository.uploadFile(filePart).toList()

        // Assert
        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)
        assertEquals(uploadResponse, (results[1] as Resource.Success).data)
    }

    @Test
    fun startProcessing_success_emitsSuccessResource_andTriggersFirestoreJob() = runTest {
        // Arrange
        val request = ProcessRequest(
            fileUrl = "http://10.0.2.2/static/scan.nii",
            fileName = "scan.nii",
            fileSize = 2048,
            cloudProvider = "firebase",
            userEmail = "researcher@lab.org"
        )
        val processResponse = ProcessResponse(jobId = "mock_job_id", estimatedTimeSeconds = 120, status = "QUEUED")
        val apiResponse = ApiResponse(success = true, message = "started", data = processResponse)
        
        coEvery { apiService.startProcessing(request) } returns Response.success(apiResponse)
        coEvery { firestoreJobRepository.createJob(any(), any(), any(), any()) } just Runs

        // Act
        val results = repository.startProcessing(request).toList()

        // Assert
        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)
        assertEquals(processResponse, (results[1] as Resource.Success).data)
        
        coVerify(exactly = 1) {
            firestoreJobRepository.createJob(
                jobId = "mock_job_id",
                userId = "test_user_id",
                fileName = "scan.nii",
                fileUrl = "http://10.0.2.2/static/scan.nii"
            )
        }
    }
}
