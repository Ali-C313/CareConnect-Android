package com.example.careconnect.domain.model

// path = Strecke zum Einzeichnen auf der Karte
data class RouteInfo(
    val distanceMeters: Double,
    val durationSeconds: Double,
    val path: List<Coordinate> = emptyList()
)
