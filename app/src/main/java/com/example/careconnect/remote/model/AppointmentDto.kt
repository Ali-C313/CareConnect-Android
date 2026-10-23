package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.Appointment
import com.google.firebase.Timestamp

// patients/{id}/appointments
data class AppointmentDto(
    val what: String? = null,
    val address: String? = null,
    val date: Timestamp? = null,
    val bringItems: List<String>? = null,
    val notes: String? = null
)
fun AppointmentDto.toAppointment(docId: String): Appointment = Appointment(
    id = docId,
    what = what ?: "",
    address = address ?: "",
    date = date?.toMillis() ?: 0L,
    bringItems = bringItems.orEmpty(),
    notes = notes ?: ""
)
fun Appointment.toDto(): AppointmentDto = AppointmentDto(
    what = what,
    address = address,
    date = date.toTimestamp(),
    bringItems = bringItems,
    notes = notes.ifBlank { null }
)
