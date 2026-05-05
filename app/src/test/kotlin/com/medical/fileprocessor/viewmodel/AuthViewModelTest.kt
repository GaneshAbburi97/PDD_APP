package com.medical.fileprocessor.viewmodel

import com.medical.fileprocessor.model.User
import com.medical.fileprocessor.repository.AuthRepository
import com.medical.fileprocessor.util.Resource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @MockK
    private lateinit var authRepository: AuthRepository

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success emits Loading and then Success`() = runTest {
        val user = User("id123", "test@example.com")
        coEvery { authRepository.login("test@example.com", "password") } returns flowOf(
            Resource.Loading(),
            Resource.Success(user)
        )

        viewModel.login("test@example.com", "password")
        
        // Initial state is null
        assertEquals(null, viewModel.authState.value)
        
        // Advance dispatcher to collect flow
        advanceUntilIdle()
        
        // Final state should be success
        assert(viewModel.authState.value is Resource.Success)
        assertEquals(user, (viewModel.authState.value as Resource.Success).data)
    }

    @Test
    fun `login failure emits Loading and then Error`() = runTest {
        val exception = Exception("Login failed")
        coEvery { authRepository.login("test@example.com", "password") } returns flowOf(
            Resource.Loading(),
            Resource.Error(exception)
        )

        viewModel.login("test@example.com", "password")
        
        advanceUntilIdle()
        
        assert(viewModel.authState.value is Resource.Error)
        assertEquals(exception, (viewModel.authState.value as Resource.Error).exception)
    }

    @Test
    fun `register success emits Loading and then Success`() = runTest {
        val user = User("id123", "test@example.com", displayName = "John Doe")
        coEvery { authRepository.register("test@example.com", "password", "John Doe") } returns flowOf(
            Resource.Loading(),
            Resource.Success(user)
        )

        viewModel.register("test@example.com", "password", "John Doe")
        
        advanceUntilIdle()
        
        assert(viewModel.authState.value is Resource.Success)
        assertEquals(user, (viewModel.authState.value as Resource.Success).data)
    }
}
