package com.example.careconnect.platform

import android.net.Uri

// Links öffnen Google Maps
fun mapsSearchUrl(query: String): String =
    "https://www.google.com/maps/search/?api=1&query=${Uri.encode(query)}"

fun mapsDirectionsUrl(latitude: Double, longitude: Double): String =
    "https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude&travelmode=driving"

// Route über mehrere Adressen, Start ist der aktuelle Standort
fun tourUrl(addresses: List<String>): String? {
    if (addresses.isEmpty()) return null
    val destination = Uri.encode(addresses.last())
    val waypoints = addresses.dropLast(1).joinToString("%7C") { Uri.encode(it) }
    return "https://www.google.com/maps/dir/?api=1&travelmode=driving&destination=$destination" +
        if (waypoints.isNotEmpty()) "&waypoints=$waypoints" else ""
}
