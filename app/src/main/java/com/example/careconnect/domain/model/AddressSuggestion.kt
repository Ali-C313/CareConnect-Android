package com.example.careconnect.domain.model

// Vorschlag aus der Adresssuche
data class AddressSuggestion(
    val formatted: String,
    val coordinate: Coordinate?
)
