package com.example.careconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.MapMarker
import com.example.careconnect.mock.MockData
import com.example.careconnect.platform.PlatformMap
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.ResourceHelper

/**
 * Karte mit abgerundeten Ecken. Ohne Marker wird ein Platzhalter angezeigt.
 */
@Composable
fun CareMap(
    markers: List<MapMarker>,
    modifier: Modifier = Modifier,
    height: Dp = 220.dp,
    emptyText: String = ResourceHelper.getString(R.string.standort_abgerufen),
    isLoading: Boolean = true,
    route: List<Coordinate> = emptyList()
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (markers.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Icon(
                        Icons.Default.LocationOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = emptyText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            PlatformMap(
                markers = markers,
                modifier = Modifier.fillMaxSize(),
                route = route
            )
        }
    }
}

@Preview
@Composable
fun CareMapPreview() {
    CareConnectTheme(darkTheme = false) {
        CareMap(
            markers = listOf(
                MapMarker("Mitarbeiter", MockData.employeeLocation, isEmployee = true),
                MapMarker("Patient", MockData.patientLocation)
            ),
            route = listOf(MockData.employeeLocation, MockData.patientLocation)
        )
    }
}
