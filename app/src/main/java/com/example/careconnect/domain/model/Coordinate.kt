package com.example.careconnect.domain.model

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class Coordinate(
    val latitude: Double,
    val longitude: Double
)

// Luftlinie in Metern
fun Coordinate.distanceTo(other: Coordinate): Double {
    val dLat = (other.latitude - latitude).toRadians()
    val dLon = (other.longitude - longitude).toRadians()
    val a = sin(dLat / 2).pow(2) +
            cos(latitude.toRadians()) * cos(other.latitude.toRadians()) * sin(dLon / 2).pow(2)
    return 2 * 6_371_000.0 * atan2(sqrt(a), sqrt(1 - a))
}

// Route erst neu holen, wenn sich der Mitarbeiter ein Stück bewegt hat
fun isSameRouteRequest(
    old: Pair<Coordinate?, Coordinate?>,
    new: Pair<Coordinate?, Coordinate?>
): Boolean {
    if (old.second != new.second) return false
    val from = old.first
    val to = new.first
    if (from == null || to == null) return from == to
    return from.distanceTo(to) < 80
}

private fun Double.toRadians() = this * PI / 180.0
