package com.example.careconnect.ui.auth

import com.example.careconnect.domain.model.AddressSuggestion

data class RegisterUiState(
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: Long = 946_728_000_000L, // 01.01.2000
    val address: String = "",
    val city: String = "",
    val phone: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val addressSuggestions: List<AddressSuggestion> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
