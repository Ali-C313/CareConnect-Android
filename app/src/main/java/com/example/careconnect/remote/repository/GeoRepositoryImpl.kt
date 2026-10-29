package com.example.careconnect.remote.repository

import com.example.careconnect.domain.model.AddressSuggestion
import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.RouteInfo
import com.example.careconnect.domain.model.distanceTo
import com.example.careconnect.domain.repository.GeoRepository
import com.example.careconnect.device.AddressGeocoder
import com.example.careconnect.remote.Secrets
import com.example.careconnect.remote.api.GeoapifyApiService
import com.example.careconnect.remote.api.OsrmApiService
import com.example.careconnect.remote.model.toAddressSuggestion
import com.example.careconnect.remote.model.toCoordinate
import com.example.careconnect.remote.model.toRouteInfo
import kotlinx.coroutines.withTimeoutOrNull

// Adresssuche und Geocoding (Handy-Geocoder, sonst Geoapify), Route über OSRM
class GeoRepositoryImpl(
    private val geoapifyApi: GeoapifyApiService,
    private val osrmApi: OsrmApiService,
    private val addressGeocoder: AddressGeocoder
) : GeoRepository {

    private val apiKey = Secrets.GEOAPIFY_API_KEY

    // Ohne echten Key gar nicht erst bei Geoapify anfragen
    private val hasApiKey = apiKey.isNotBlank() && !apiKey.startsWith("DEIN_")

    override suspend fun searchAddress(query: String): Result<List<AddressSuggestion>> = runCatching {
        if (!hasApiKey || query.trim().length < 3) return@runCatching emptyList()
        val response = geoapifyApi.autocomplete(text = query, apiKey = apiKey)
        if (!response.isSuccessful) throw Exception("HTTP ${response.code()}")
        response.body()?.results.orEmpty().map { it.toAddressSuggestion() }
    }

    override suspend fun geocode(address: String): Result<Coordinate?> = runCatching {
        if (address.isBlank()) return@runCatching null

        // Erst der Geocoder vom Handy (braucht keinen Key), dann Geoapify
        val local = withTimeoutOrNull(10_000) { runCatching { addressGeocoder.geocode(address) }.getOrNull() }
        if (local != null) return@runCatching local
        if (!hasApiKey) return@runCatching null

        val response = geoapifyApi.search(text = address, apiKey = apiKey)
        if (!response.isSuccessful) throw Exception("HTTP ${response.code()}")
        response.body()?.results?.firstOrNull()?.toCoordinate()
    }

    // Erst OSRM (Straßenverlauf, kein Key), dann Geoapify, zur Not grob geschätzt
    override suspend fun getRoute(from: Coordinate, to: Coordinate): Result<RouteInfo?> = runCatching {
        osrmRoute(from, to)
            ?: geoapifyRoute(from, to)
            ?: estimateRoute(from, to)
    }

    private suspend fun osrmRoute(from: Coordinate, to: Coordinate): RouteInfo? = withTimeoutOrNull(8_000) {
        runCatching {
            val points = "${from.longitude},${from.latitude};${to.longitude},${to.latitude}"
            val response = osrmApi.route(points)
            val body = response.body()
            if (!response.isSuccessful || body == null || body.code != "Ok") null
            else body.routes.firstOrNull()?.toRouteInfo()
        }.getOrNull()
    }

    private suspend fun geoapifyRoute(from: Coordinate, to: Coordinate): RouteInfo? {
        if (!hasApiKey) return null
        return runCatching {
            val waypoints = "${from.latitude},${from.longitude}|${to.latitude},${to.longitude}"
            val properties = geoapifyApi.route(waypoints = waypoints, apiKey = apiKey).body()?.features?.firstOrNull()?.properties
            val distance = properties?.distance
            val time = properties?.time
            if (distance != null && time != null) RouteInfo(distance, time, listOf(from, to)) else null
        }.getOrNull()
    }

    // Luftlinie * 1,3 und ca. 30 km/h in der Stadt
    private fun estimateRoute(from: Coordinate, to: Coordinate): RouteInfo {
        val meters = from.distanceTo(to) * 1.3
        return RouteInfo(
            distanceMeters = meters,
            durationSeconds = meters / (30_000.0 / 3600.0),
            path = listOf(from, to)
        )
    }
}
