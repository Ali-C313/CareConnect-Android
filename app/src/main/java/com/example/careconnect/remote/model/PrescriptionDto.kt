package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.Prescription
import com.example.careconnect.domain.model.PrescriptionStatus
import com.google.firebase.Timestamp

// organizations/{id}/prescriptions
data class PrescriptionDto(
    val fromOrgId: String? = null,
    val fromOrgName: String? = null,
    val toOrgId: String? = null,
    val toOrgName: String? = null,
    val patientId: String? = null,
    val patientName: String? = null,
    val title: String? = null,
    val details: String? = null,
    val frequency: String? = null,
    val startDate: Timestamp? = null,
    val createdBy: String? = null,
    val createdByName: String? = null,
    val createdAt: Timestamp? = null,
    val status: String? = null,
    val orderId: String? = null
)

fun PrescriptionDto.toPrescription(id: String): Prescription = Prescription(
    id = id,
    fromOrgId = fromOrgId ?: "",
    fromOrgName = fromOrgName ?: "",
    toOrgId = toOrgId ?: "",
    toOrgName = toOrgName ?: "",
    patientId = patientId ?: "",
    patientName = patientName ?: "",
    title = title ?: "",
    details = details ?: "",
    frequency = frequency ?: "",
    startDate = startDate?.toMillis(),
    createdBy = createdBy ?: "",
    createdByName = createdByName ?: "",
    createdAt = createdAt?.toMillis(),
    status = PrescriptionStatus.fromValue(status),
    orderId = orderId ?: ""
)

fun Prescription.toDto(): PrescriptionDto = PrescriptionDto(
    fromOrgId = fromOrgId,
    fromOrgName = fromOrgName,
    toOrgId = toOrgId,
    toOrgName = toOrgName,
    patientId = patientId,
    patientName = patientName,
    title = title,
    details = details,
    frequency = frequency,
    startDate = startDate?.toTimestamp(),
    createdBy = createdBy,
    createdByName = createdByName,
    createdAt = createdAt?.toTimestamp(),
    status = status.value,
    orderId = orderId
)
