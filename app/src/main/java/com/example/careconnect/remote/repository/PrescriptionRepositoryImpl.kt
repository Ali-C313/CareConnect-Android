package com.example.careconnect.remote.repository

import com.example.careconnect.domain.model.LinkedOrg
import com.example.careconnect.domain.model.Prescription
import com.example.careconnect.domain.model.PrescriptionStatus
import com.example.careconnect.domain.repository.PrescriptionRepository
import com.example.careconnect.remote.CollectionNames
import com.example.careconnect.remote.listenAsFlow
import com.example.careconnect.remote.model.LinkedOrgDto
import com.example.careconnect.remote.model.LinkedOrgsUpdateDto
import com.example.careconnect.remote.model.PatientDto
import com.example.careconnect.remote.model.PrescriptionDto
import com.example.careconnect.remote.model.toDto
import com.example.careconnect.remote.model.toPatient
import com.example.careconnect.remote.model.toPrescription
import com.example.careconnect.remote.org
import com.example.careconnect.session.OrgSession
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PrescriptionRepositoryImpl(
    private val db: FirebaseFirestore,
    private val session: OrgSession
) : PrescriptionRepository {

    private fun prescriptionRef(orgId: String, id: String) =
        db.org(orgId).collection(CollectionNames.PRESCRIPTIONS).document(id)

    private fun patientRef(orgId: String, patientId: String) =
        db.org(orgId).collection(CollectionNames.PATIENTS).document(patientId)

    override fun getPrescriptions(): Flow<List<Prescription>> = session.inOrg(emptyList()) { orgId ->
        db.org(orgId).collection(CollectionNames.PRESCRIPTIONS).listenAsFlow()
            .map { snapshot ->
                snapshot.documents
                    .mapNotNull { doc -> runCatching { doc.toObject<PrescriptionDto>()?.toPrescription(doc.id) }.getOrNull() }
                    .sortedByDescending { it.createdAt ?: 0L }
            }
            .catch { emit(emptyList()) }
    }

    override suspend fun createPrescription(prescription: Prescription): Result<Unit> = runCatching {
        val id = prescription.id.ifEmpty { UUID.randomUUID().toString() }
        val dto = prescription.copy(id = id).toDto()
        val batch = db.batch()
        batch.set(prescriptionRef(prescription.fromOrgId, id), dto)
        batch.set(prescriptionRef(prescription.toOrgId, id), dto)
        batch.commit().await()
        Unit
    }

    // Pflegedienst ändert den Status in beiden Kopien
    override suspend fun updateStatus(
        prescription: Prescription,
        status: PrescriptionStatus,
        orderId: String
    ): Result<Unit> = runCatching {
        val batch = db.batch()
        listOf(prescription.fromOrgId, prescription.toOrgId).forEach { orgId ->
            batch.update(
                prescriptionRef(orgId, prescription.id),
                mapOf(
                    "status" to status.value,
                    "orderId" to orderId.ifEmpty { prescription.orderId }
                )
            )
        }
        batch.commit().await()
        Unit
    }

    override suspend fun withdraw(prescription: Prescription): Result<Unit> = runCatching {
        val batch = db.batch()
        batch.delete(prescriptionRef(prescription.fromOrgId, prescription.id))
        batch.delete(prescriptionRef(prescription.toOrgId, prescription.id))
        batch.commit().await()
        Unit
    }

    override fun observeLinkedOrgs(orgId: String, patientId: String): Flow<List<LinkedOrg>> =
        patientRef(orgId, patientId).listenAsFlow()
            .map { doc ->
                if (!doc.exists()) emptyList()
                else runCatching { doc.toObject<PatientDto>()?.toPatient(doc.id)?.linkedOrgs.orEmpty() }
                    .getOrDefault(emptyList())
            }
            .catch { emit(emptyList()) }

    override suspend fun setLinkedOrgs(orgId: String, patientId: String, links: List<LinkedOrg>): Result<Unit> = runCatching {
        val dto = LinkedOrgsUpdateDto(
            linkedOrgIds = links.map { it.orgId },
            linkedOrgs = links.map { LinkedOrgDto(it.orgId, it.orgName) }
        )
        patientRef(orgId, patientId).set(dto, SetOptions.merge()).await()
        Unit
    }
}
