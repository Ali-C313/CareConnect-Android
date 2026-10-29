package com.example.careconnect.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CoordinateTest {

    private val berlin = Coordinate(52.5200, 13.4050)
    private val hamburg = Coordinate(53.5511, 9.9937)

    @Test
    fun distanceTo_samePoint_isZero() {
        assertEquals(0.0, berlin.distanceTo(berlin), 0.001)
    }

    @Test
    fun distanceTo_berlinHamburg_isAbout255Km() {
        assertEquals(255_000.0, berlin.distanceTo(hamburg), 5_000.0)
    }

    @Test
    fun isSameRouteRequest_whenMovedOnlyALittle_returnsTrue() {
        val moved = Coordinate(52.5201, 13.4050)
        assertTrue(isSameRouteRequest(berlin to hamburg, moved to hamburg))
    }

    @Test
    fun isSameRouteRequest_whenTargetChanged_returnsFalse() {
        assertFalse(isSameRouteRequest(berlin to hamburg, berlin to berlin))
    }
}
