package com.example.careconnect.domain.model

import com.example.careconnect.R
import com.example.careconnect.util.ResourceHelper

// Auftrag für einen Patienten
data class Order(
    val id: String,
    val state: OrderState = OrderState.OFFEN,
    val notes: List<String> = emptyList(),
    val description: String = "",
    val patientId: String = "",
    val patientName: String = "",
    val createdBy: String = "",
    val employeeId: String = "",
    val dueDate: Long? = null,
    val createdAt: Long? = null,
    val report: CompletionReport? = null
) {
    val displayTitle: String
        get() = ResourceHelper.getString(R.string.auftrag_nummer, id.take(6))
}
