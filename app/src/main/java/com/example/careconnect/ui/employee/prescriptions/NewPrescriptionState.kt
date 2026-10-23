package com.example.careconnect.ui.employee.prescriptions

import com.example.careconnect.util.DateHelper.currentTimeMillis

data class NewPrescriptionState(
    val patientId: String = "",
    val toOrgId: String = "",
    val title: String = "",
    val details: String = "",
    val frequency: String = "",
    val startDate: Long = currentTimeMillis(),
    val isSaving: Boolean = false,
    val errorMessage: String? = null
) {
    val canSave: Boolean
        get() = patientId.isNotEmpty() && toOrgId.isNotEmpty() && title.isNotBlank() && !isSaving
}
