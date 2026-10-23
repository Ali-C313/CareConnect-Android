package com.example.careconnect.platform

import com.example.careconnect.domain.model.Coordinate

// Adresse -> Koordinate ohne API-Key über den Android-Geocoder
interface PlatformGeocoder {
    suspend fun geocode(address: String): Coordinate?
}
