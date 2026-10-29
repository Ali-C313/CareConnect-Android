package com.example.careconnect.mock.repository

import com.example.careconnect.domain.model.LinkedOrg
import com.example.careconnect.domain.model.Prescription
import com.example.careconnect.domain.model.PrescriptionStatus
import com.example.careconnect.domain.repository.PrescriptionRepository
import com.example.careconnect.mock.MockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

// Beispielverordnungen für die Previews
class PrescriptionRepositoryMock : PrescriptionRepository {

    private val prescriptions = MutableStateFlow(MockData.prescriptions)
    private val linkedOrgs = MutableStateFlow(MockData.patient.linkedOrgs)

    override fun getPrescriptions(): Flow<List<Prescription>> = prescriptions

    override suspend fun createPrescription(prescription: Prescription): Result<Unit> = runCatching {
        prescriptions.update { it + prescription.copy(id = "rx${it.size + 1}") }
    }

    override suspend fun updateStatus(
        prescription: Prescription,
        status: PrescriptionStatus,
        orderId: String
    ): Result<Unit> = runCatching {
        prescriptions.update { list ->
            list.map { if (it.id == prescription.id) it.copy(status = status, orderId = orderId) else it }
        }
    }

    override suspend fun withdraw(prescription: Prescription): Result<Unit> = runCatching {
        prescriptions.update { list -> list.filterNot { it.id == prescription.id } }
    }

    override fun observeLinkedOrgs(orgId: String, patientId: String): Flow<List<LinkedOrg>> = linkedOrgs

    override suspend fun setLinkedOrgs(orgId: String, patientId: String, links: List<LinkedOrg>): Result<Unit> = runCatching {
        linkedOrgs.update { links }
    }
}
