package com.example.careconnect.device

import com.example.careconnect.domain.model.Coordinate

// Adresse -> Koordinate ohne API-Key über den Android-Geocoder
interface AddressGeocoder {
    suspend fun geocode(address: String): Coordinate?
}
