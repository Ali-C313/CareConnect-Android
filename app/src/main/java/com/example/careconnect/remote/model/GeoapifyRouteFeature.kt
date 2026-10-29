package com.example.careconnect.remote.model

// Eine Route aus der Geoapify-Antwort
data class GeoapifyRouteFeature(
    val properties: GeoapifyRouteProperties = GeoapifyRouteProperties()
)
