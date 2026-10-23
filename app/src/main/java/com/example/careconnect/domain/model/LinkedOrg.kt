package com.example.careconnect.domain.model

// Pflegedienst, an den eine Praxis Verordnungen schicken darf (Freigabe des Patienten)
data class LinkedOrg(
    val orgId: String,
    val orgName: String
)
