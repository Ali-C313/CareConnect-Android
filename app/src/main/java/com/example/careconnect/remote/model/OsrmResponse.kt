package com.example.careconnect.remote.model

// Antwort vom OSRM-Routing (geometries=geojson)
data class OsrmResponse(
    val code: String = "",
    val routes: List<OsrmRoute> = emptyList()
)
