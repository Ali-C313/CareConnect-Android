package com.example.careconnect.ui.auth

// Eingaben und Fehler im Login
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isFormValid: Boolean
        get() = email.isNotBlank() && password.isNotBlank()
}
