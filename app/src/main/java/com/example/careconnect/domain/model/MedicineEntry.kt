package com.example.careconnect.domain.model

data class MedicineEntry(
    val id: String = "",
    val name: String,
    val dosage: String,
    val timeOfDay: TimeOfDay
)
