package com.example.careconnect.remote.model

import com.google.firebase.Timestamp

// Nur die Organisations-ID, damit der Patient seine Anfragen wiederfindet
data class JoinRequestRefDto(
    val orgId: String? = null,
    val createdAt: Timestamp? = null
)
