package com.example.careconnect.remote.model

// Geocoding / Autocomplete mit format=json
data class GeoapifyResponse(
    val results: List<GeoapifyResult> = emptyList()
)
