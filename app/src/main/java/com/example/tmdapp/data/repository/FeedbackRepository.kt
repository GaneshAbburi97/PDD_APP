package com.example.tmdapp.data.repository

import com.example.tmdapp.data.remote.ApiClient
import com.example.tmdapp.data.remote.FeedbackRequest

class FeedbackRepository {
    private val api = ApiClient.apiService

    suspend fun submitFeedback(name: String, message: String) {
        api.submitFeedback(FeedbackRequest(name, message))
    }
}
