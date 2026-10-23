package com.example.careconnect.domain.model

data class EmployeeDocument(
    val id: String,
    val fileName: String,
    val downloadUrl: String,
    val uploadedAt: Long?
)
