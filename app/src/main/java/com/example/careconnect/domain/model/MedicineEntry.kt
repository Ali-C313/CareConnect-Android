package com.example.careconnect.domain.model

// Medikament, das der Patient selbst einträgt
data class MedicineEntry(
    val id: String = "",
    val name: String,
    val dosage: String,
    val timeOfDay: TimeOfDay
)
