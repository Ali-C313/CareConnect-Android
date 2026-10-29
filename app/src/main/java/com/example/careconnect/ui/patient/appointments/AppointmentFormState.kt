package com.example.careconnect.ui.patient.appointments

import com.example.careconnect.domain.model.AddressSuggestion
import com.example.careconnect.util.DateHelper.currentTimeMillis

// Eingaben für einen Termin
data class AppointmentFormState(
    val editingId: String? = null,
    val what: String = "",
    val address: String = "",
    val date: Long = currentTimeMillis(),
    val bringItems: List<String> = emptyList(),
    val notes: String = "",
    val addressSuggestions: List<AddressSuggestion> = emptyList()
) {
    val canSave: Boolean
        get() = what.isNotBlank() && address.isNotBlank()
}
