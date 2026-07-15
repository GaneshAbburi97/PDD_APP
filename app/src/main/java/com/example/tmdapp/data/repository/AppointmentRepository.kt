package com.example.tmdapp.data.repository

import com.example.tmdapp.data.model.Appointment
import com.example.tmdapp.data.remote.ApiClient
import com.example.tmdapp.data.remote.BookAppointmentRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AppointmentRepository {
    private val api = ApiClient.apiService

    fun getAppointments(): Flow<List<Appointment>> = flow {
        try {
            emit(api.getAppointments())
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }

    suspend fun bookAppointment(doctorName: String, date: String, time: String, reason: String): Appointment {
        return api.bookAppointment(BookAppointmentRequest(doctorName, date, time, reason))
    }
}
