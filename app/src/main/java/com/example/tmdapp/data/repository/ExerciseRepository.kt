package com.example.tmdapp.data.repository

import com.example.tmdapp.data.model.ExerciseRecord
import com.example.tmdapp.data.remote.ApiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExerciseRepository {
    private val api = ApiClient.apiService

    suspend fun saveRecord(userId: String, exerciseName: String, durationSec: Int, category: String): ExerciseRecord {
        val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val newRecord = ExerciseRecord(
            userId = userId,
            date = date,
            exerciseName = exerciseName,
            durationSec = durationSec,
            category = category,
            timestamp = System.currentTimeMillis()
        )
        return api.addExerciseRecord(newRecord)
    }

    fun getRecordsForUser(userId: String): Flow<List<ExerciseRecord>> = flow {
        try {
            val records = api.getExerciseRecords()
            emit(records)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }
}
