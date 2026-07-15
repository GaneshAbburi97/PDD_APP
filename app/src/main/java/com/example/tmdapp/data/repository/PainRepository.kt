package com.example.tmdapp.data.repository

import com.example.tmdapp.data.model.PainRecord
import com.example.tmdapp.data.remote.ApiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PainRepository {
    private val api = ApiClient.apiService

    suspend fun saveRecord(userId: String, pain: Int, stress: Int, location: String, type: String): PainRecord {
        val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val newRecord = PainRecord(
            userId = userId,
            date = date,
            painLevel = pain,
            stressLevel = stress,
            location = location,
            type = type,
            timestamp = System.currentTimeMillis()
        )
        return api.addPainRecord(newRecord)
    }

    fun getRecordsForUser(userId: String): Flow<List<PainRecord>> = flow {
        try {
            val records = api.getPainRecords()
            emit(records)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }
}
