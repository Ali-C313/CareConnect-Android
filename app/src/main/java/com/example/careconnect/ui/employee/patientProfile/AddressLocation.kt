package com.example.careconnect.ui.employee.patientProfile

import com.example.careconnect.domain.model.Coordinate

// Zustand der Karte: sucht noch, gefunden oder nicht gefunden
sealed interface AddressLocation {
    data object Loading : AddressLocation
    data class Found(val coordinate: Coordinate) : AddressLocation
    data object NotFound : AddressLocation
}
