package com.example.tmdapp.data.repository

import com.example.tmdapp.data.model.AssessmentRecord
import com.example.tmdapp.data.remote.ApiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AssessmentRepository {
    private val api = ApiClient.apiService

    fun getAssessmentRecordsForUser(userId: String): Flow<List<AssessmentRecord>> = flow {
        try {
            val records = api.getAssessmentRecords()
            emit(records)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }

    suspend fun saveAssessmentRecord(record: AssessmentRecord) {
        api.addAssessmentRecord(record)
    }
}
