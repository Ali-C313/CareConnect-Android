package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.AddressSuggestion
import com.example.careconnect.domain.model.Coordinate

data class GeoapifyResult(
    val formatted: String = "",
    val lat: Double? = null,
    val lon: Double? = null
)
fun GeoapifyResult.toAddressSuggestion(): AddressSuggestion = AddressSuggestion(
    formatted = formatted,
    coordinate = toCoordinate()
)
fun GeoapifyResult.toCoordinate(): Coordinate? {
    val latitude = lat ?: return null
    val longitude = lon ?: return null
    return Coordinate(latitude, longitude)
}
