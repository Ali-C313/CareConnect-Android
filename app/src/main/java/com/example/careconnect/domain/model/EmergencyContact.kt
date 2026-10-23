package com.example.careconnect.domain.model

data class EmergencyContact(
    val name: String = "",
    val relation: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val age: Int? = null
)
