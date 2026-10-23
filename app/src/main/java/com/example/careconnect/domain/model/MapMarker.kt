package com.example.careconnect.domain.model

data class MapMarker(
    val title: String,
    val coordinate: Coordinate,
    val isEmployee: Boolean = false
)
