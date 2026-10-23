package com.example.careconnect.domain.repository

import com.example.careconnect.domain.model.LinkedOrg
import com.example.careconnect.domain.model.Prescription
import com.example.careconnect.domain.model.PrescriptionStatus
import kotlinx.coroutines.flow.Flow

interface PrescriptionRepository {
    // Eingehende und ausgehende Verordnungen der aktuellen Organisation
    fun getPrescriptions(): Flow<List<Prescription>>

    // Legt die Verordnung bei Praxis und Pflegedienst an
    suspend fun createPrescription(prescription: Prescription): Result<Unit>
    suspend fun updateStatus(prescription: Prescription, status: PrescriptionStatus, orderId: String = ""): Result<Unit>
    suspend fun withdraw(prescription: Prescription): Result<Unit>

    // Freigaben des Patienten in einer bestimmten Organisation
    fun observeLinkedOrgs(orgId: String, patientId: String): Flow<List<LinkedOrg>>
    suspend fun setLinkedOrgs(orgId: String, patientId: String, links: List<LinkedOrg>): Result<Unit>
}
