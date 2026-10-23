package com.example.careconnect.domain.model

data class Appointment(
    val id: String = "",
    val what: String,
    val address: String,
    val date: Long,
    val bringItems: List<String> = emptyList(),
    val notes: String = ""
)
