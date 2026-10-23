package com.example.careconnect.domain.model

// Leistungsnachweis beim Abschließen
data class CompletionReport(
    val note: String,
    // Striche als "x,y;x,y|x,y;..." mit Werten zwischen 0 und 1, leer = keine Unterschrift
    val signature: String,
    val signedBy: String,
    val completedAt: Long?,
    val completedBy: String,
    val completedByName: String
)
