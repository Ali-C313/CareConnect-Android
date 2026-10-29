package com.example.careconnect.remote.repository

import com.example.careconnect.domain.model.Appointment
import com.example.careconnect.domain.repository.AppointmentRepository
import com.example.careconnect.remote.CollectionNames
import com.example.careconnect.remote.listenAsFlow
import com.example.careconnect.remote.model.AppointmentDto
import com.example.careconnect.remote.model.toAppointment
import com.example.careconnect.remote.model.toDto
import com.example.careconnect.remote.org
import com.example.careconnect.session.OrgSession
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

// Termine unter patients/{id}/appointments
class AppointmentRepositoryImpl(
    private val db: FirebaseFirestore,
    private val session: OrgSession
) : AppointmentRepository {

    private fun appointmentsRef(patientId: String, orgId: String = session.requireOrgId()) = db.org(orgId)
        .collection(CollectionNames.PATIENTS)
        .document(patientId)
        .collection(CollectionNames.APPOINTMENTS)

    override fun getAppointments(patientId: String): Flow<List<Appointment>> = session.inOrg(emptyList()) { orgId ->
        appointmentsRef(patientId, orgId).listenAsFlow()
            .map { snapshot ->
                snapshot.documents
                    .mapNotNull { doc ->
                        runCatching { doc.toObject<AppointmentDto>()?.toAppointment(doc.id) }.getOrNull()
                    }
                    .sortedBy { it.date }
            }
            .catch { emit(emptyList()) }
    }

    override suspend fun addAppointment(patientId: String, appointment: Appointment): Result<Unit> = runCatching {
        appointmentsRef(patientId).add(appointment.toDto()).await()
        Unit
    }

    override suspend fun updateAppointment(patientId: String, appointment: Appointment): Result<Unit> = runCatching {
        appointmentsRef(patientId).document(appointment.id).set(appointment.toDto()).await()
        Unit
    }

    override suspend fun deleteAppointment(patientId: String, appointmentId: String): Result<Unit> = runCatching {
        appointmentsRef(patientId).document(appointmentId).delete().await()
        Unit
    }
}
