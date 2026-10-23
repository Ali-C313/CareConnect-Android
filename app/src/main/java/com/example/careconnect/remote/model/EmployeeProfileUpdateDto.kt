package com.example.careconnect.remote.model

// Nur für Updates mit merge = true
data class EmployeeProfileUpdateDto(
    val firstName: String,
    val lastName: String,
    val role: String,
    val email: String
)
