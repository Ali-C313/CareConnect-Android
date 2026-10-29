package com.example.careconnect.mock.repository

import com.example.careconnect.domain.model.Appointment
import com.example.careconnect.domain.repository.AppointmentRepository
import com.example.careconnect.mock.MockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

// Beispieltermine für die Previews
class AppointmentRepositoryMock : AppointmentRepository {

    private val appointments = MutableStateFlow(MockData.appointments)

    override fun getAppointments(patientId: String): Flow<List<Appointment>> = appointments

    override suspend fun addAppointment(patientId: String, appointment: Appointment): Result<Unit> = runCatching {
        appointments.update { it + appointment.copy(id = "a${it.size + 1}") }
    }

    override suspend fun updateAppointment(patientId: String, appointment: Appointment): Result<Unit> = runCatching {
        appointments.update { list -> list.map { if (it.id == appointment.id) appointment else it } }
    }

    override suspend fun deleteAppointment(patientId: String, appointmentId: String): Result<Unit> = runCatching {
        appointments.update { list -> list.filterNot { it.id == appointmentId } }
    }
}
