package com.example.careconnect.device

import com.example.careconnect.domain.model.Coordinate

// Umsetzung mit FusedLocation in AndroidLocationTracker
interface LocationTracker {
    fun start(onLocation: (Coordinate) -> Unit)
    fun stop()
}
