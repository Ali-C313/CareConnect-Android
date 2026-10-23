package com.example.careconnect.domain.repository

import com.example.careconnect.domain.model.AddressSuggestion
import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.RouteInfo

interface GeoRepository {
    suspend fun searchAddress(query: String): Result<List<AddressSuggestion>>
    suspend fun geocode(address: String): Result<Coordinate?>
    suspend fun getRoute(from: Coordinate, to: Coordinate): Result<RouteInfo?>
}
