package com.example.careconnect.mock.repository

import com.example.careconnect.domain.model.AddressSuggestion
import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.RouteInfo
import com.example.careconnect.domain.repository.GeoRepository
import com.example.careconnect.mock.MockData

// Feste Adressen und Routen für die Previews
class GeoRepositoryMock : GeoRepository {

    override suspend fun searchAddress(query: String): Result<List<AddressSuggestion>> = runCatching {
        listOf(AddressSuggestion("Torstraße 12, 10119 Berlin", MockData.patientLocation))
    }

    override suspend fun geocode(address: String): Result<Coordinate?> = runCatching {
        MockData.patientLocation
    }

    override suspend fun getRoute(from: Coordinate, to: Coordinate): Result<RouteInfo?> = runCatching {
        RouteInfo(distanceMeters = 1_400.0, durationSeconds = 300.0, path = listOf(from, to))
    }
}
