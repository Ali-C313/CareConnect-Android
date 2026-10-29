package com.example.careconnect.domain.repository

import com.example.careconnect.domain.model.Appointment
import kotlinx.coroutines.flow.Flow

// Termine eines Patienten laden und bearbeiten
interface AppointmentRepository {
    fun getAppointments(patientId: String): Flow<List<Appointment>>
    suspend fun addAppointment(patientId: String, appointment: Appointment): Result<Unit>
    suspend fun updateAppointment(patientId: String, appointment: Appointment): Result<Unit>
    suspend fun deleteAppointment(patientId: String, appointmentId: String): Result<Unit>
}
