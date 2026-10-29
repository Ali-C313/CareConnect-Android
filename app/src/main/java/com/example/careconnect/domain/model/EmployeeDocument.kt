package com.example.careconnect.domain.model

// PDF-Dokument eines Mitarbeiters
data class EmployeeDocument(
    val id: String,
    val fileName: String,
    val downloadUrl: String,
    val uploadedAt: Long?
)
