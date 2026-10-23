package com.example.careconnect.domain.model

/**
 * Verordnung einer Praxis/Klinik an einen Pflegedienst,
 * z.B. "Verbandswechsel 3x pro Woche". Liegt bei beiden Organisationen.
 */
data class Prescription(
    val id: String = "",
    val fromOrgId: String,
    val fromOrgName: String,
    val toOrgId: String,
    val toOrgName: String,
    val patientId: String,
    val patientName: String,
    val title: String,
    val details: String = "",
    val frequency: String = "",
    val startDate: Long? = null,
    val createdBy: String = "",
    val createdByName: String = "",
    val createdAt: Long? = null,
    val status: PrescriptionStatus = PrescriptionStatus.OFFEN,
    val orderId: String = ""
)
