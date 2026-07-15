package com.example.tmdapp.data.repository

import com.example.tmdapp.data.model.WellnessRecord
import com.example.tmdapp.data.remote.ApiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*

class WellnessRepository {
    private val api = ApiClient.apiService

    suspend fun saveWellnessRecord(
        userId: String,
        sleepQuality: String,
        jawStiffness: String,
        teethGrinding: Boolean,
        mood: String,
        waterIntake: Int,
        energyLevel: Int,
        notes: String
    ): WellnessRecord {
        val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val record = WellnessRecord(
            userId = userId,
            date = date,
            sleepQuality = sleepQuality,
            jawStiffness = jawStiffness,
            teethGrinding = teethGrinding,
            mood = mood,
            waterIntake = waterIntake,
            energyLevel = energyLevel,
            notes = notes,
            timestamp = System.currentTimeMillis()
        )
        return api.addWellnessRecord(record)
    }

    fun getWellnessRecordsForUser(userId: String): Flow<List<WellnessRecord>> = flow {
        try {
            val records = api.getWellnessRecords()
            emit(records)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }
}
