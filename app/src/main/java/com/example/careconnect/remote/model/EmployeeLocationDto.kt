package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.Coordinate
import com.google.firebase.Timestamp

// employeeLocations/{employeeId}
data class EmployeeLocationDto(
    val lat: Double? = null,
    val lon: Double? = null,
    val updatedAt: Timestamp? = null
)
fun EmployeeLocationDto.toCoordinate(): Coordinate? {
    val latitude = lat ?: return null
    val longitude = lon ?: return null
    return Coordinate(latitude, longitude)
}
