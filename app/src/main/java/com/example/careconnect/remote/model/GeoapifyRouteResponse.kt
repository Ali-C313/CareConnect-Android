package com.example.careconnect.remote.model

// Routing (GeoJSON)
data class GeoapifyRouteResponse(
    val features: List<GeoapifyRouteFeature> = emptyList()
)
