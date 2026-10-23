package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.RouteInfo

data class OsrmRoute(
    val distance: Double = 0.0,
    val duration: Double = 0.0,
    val geometry: OsrmGeometry = OsrmGeometry()
)
fun OsrmRoute.toRouteInfo(): RouteInfo = RouteInfo(
    distanceMeters = distance,
    durationSeconds = duration,
    path = geometry.coordinates
        .filter { it.size >= 2 }
        .map { Coordinate(latitude = it[1], longitude = it[0]) }
)
