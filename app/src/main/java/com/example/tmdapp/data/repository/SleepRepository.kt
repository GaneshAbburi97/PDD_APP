package com.example.tmdapp.data.repository

import com.example.tmdapp.data.model.SleepRecord
import com.example.tmdapp.data.remote.ApiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*

class SleepRepository {
    private val api = ApiClient.apiService

    suspend fun saveSleepRecord(
        userId: String,
        sleepHours: Float,
        sleepQuality: String,
        jawClenching: Boolean,
        morningStiffness: String,
        wakeupFeeling: String,
        notes: String
    ): SleepRecord {
        val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val record = SleepRecord(
            userId = userId,
            date = date,
            sleepHours = sleepHours,
            sleepQuality = sleepQuality,
            jawClenching = jawClenching,
            morningStiffness = morningStiffness,
            wakeupFeeling = wakeupFeeling,
            notes = notes,
            timestamp = System.currentTimeMillis()
        )
        return api.addSleepRecord(record)
    }

    fun getSleepRecordsForUser(userId: String): Flow<List<SleepRecord>> = flow {
        try {
            val records = api.getSleepRecords()
            emit(records)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }
}
