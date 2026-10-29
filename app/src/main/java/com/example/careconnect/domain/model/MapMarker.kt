package com.example.careconnect.domain.model

// Markierung auf der Karte, isEmployee zeigt das Auto
data class MapMarker(
    val title: String,
    val coordinate: Coordinate,
    val isEmployee: Boolean = false
)
