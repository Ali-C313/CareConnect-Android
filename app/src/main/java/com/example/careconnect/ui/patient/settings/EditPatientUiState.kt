package com.example.careconnect.ui.patient.settings

import com.example.careconnect.domain.model.AddressSuggestion
import com.example.careconnect.domain.model.Patient

// Eingaben im Profil-Editor der Patienten
data class EditPatientUiState(
    val patient: Patient? = null,
    val addressSuggestions: List<AddressSuggestion> = emptyList(),
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isSaving: Boolean = false,
    val isChangingPassword: Boolean = false,
    val message: String? = null
)
