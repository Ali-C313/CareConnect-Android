package com.example.careconnect.ui.employee.settings

// Eingaben im Profil-Editor der Mitarbeiter
data class EditEmployeeUiState(
    val firstName: String = "",
    val lastName: String = "",
    val role: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isSaving: Boolean = false,
    val isChangingPassword: Boolean = false,
    val message: String? = null
)
