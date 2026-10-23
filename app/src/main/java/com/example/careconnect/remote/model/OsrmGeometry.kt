package com.example.careconnect.remote.model

// GeoJSON: [lon, lat]
data class OsrmGeometry(
    val coordinates: List<List<Double>> = emptyList()
)
