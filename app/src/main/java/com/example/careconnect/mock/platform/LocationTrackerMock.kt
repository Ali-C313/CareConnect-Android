package com.example.careconnect.mock.platform

import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.mock.MockData
import com.example.careconnect.platform.LocationTracker

// Liefert sofort einen festen Standort
class LocationTrackerMock : LocationTracker {

    override fun start(onLocation: (Coordinate) -> Unit) {
        onLocation(MockData.employeeLocation)
    }

    override fun stop() = Unit
}
