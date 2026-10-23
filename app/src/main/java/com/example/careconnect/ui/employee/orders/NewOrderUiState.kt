package com.example.careconnect.ui.employee.orders

import com.example.careconnect.util.DateHelper.currentTimeMillis

data class NewOrderUiState(
    val existingOrderId: String? = null,
    val selectedPatientId: String = "",
    val description: String = "",
    val dueDate: Long = currentTimeMillis(),
    val isSaving: Boolean = false,
    val errorMessage: String? = null
) {
    val isEditing: Boolean
        get() = existingOrderId != null

    val canSave: Boolean
        get() = selectedPatientId.isNotEmpty() && description.isNotBlank() && !isSaving
}
