package com.example.careconnect.ui.patient.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.MapMarker
import com.example.careconnect.domain.model.RouteInfo
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.CareMap
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.DateHelper.currentTimeMillis
import com.example.careconnect.util.DateHelper.toTimeString
import com.example.careconnect.util.ResourceHelper
import com.example.careconnect.util.StringHelper.toKmString
import com.example.careconnect.util.StringHelper.toMinutesString

// Karte mit Auto und Strecke, vorher fragt die App einmal nach
@Composable
fun LiveSection(
    showLiveLocation: Boolean?,
    employeeCoordinate: Coordinate?,
    homeCoordinate: Coordinate?,
    route: RouteInfo?,
    onAllow: () -> Unit,
    onDeny: () -> Unit
) {
    SectionCard(title = stringResource(R.string.live_standort), icon = Icons.Default.MyLocation) {
        if (showLiveLocation != true) {
            Text(stringResource(R.string.live_standort_anzeigen_frage), style = MaterialTheme.typography.titleMedium)
            Text(
                if (showLiveLocation == false) stringResource(R.string.hast_live_standort_ausgeschaltet) else stringResource(R.string.live_standort_hinweis),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onAllow) { Text(stringResource(R.string.anzeigen)) }
                if (showLiveLocation == null) {
                    OutlinedButton(onClick = onDeny) { Text(stringResource(R.string.nein_danke)) }
                }
            }
        } else {
            val markers = buildList {
                employeeCoordinate?.let { add(MapMarker(ResourceHelper.getString(R.string.mitarbeiter), it, isEmployee = true)) }
                homeCoordinate?.let { add(MapMarker(ResourceHelper.getString(R.string.ankunft), it)) }
            }
            CareMap(
                markers = markers,
                height = 300.dp,
                emptyText = stringResource(R.string.karte_geladen),
                route = route?.path ?: emptyList()
            )

            if (employeeCoordinate == null) {
                Text(
                    stringResource(R.string.standort_erscheint_hinweis),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            route?.let {
                val arrival = currentTimeMillis() + (it.durationSeconds * 1000).toLong()
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoRow(Icons.Default.Straighten, stringResource(R.string.entfernung), it.distanceMeters.toKmString(), Modifier.weight(1f))
                    InfoRow(
                        Icons.Default.DirectionsCar,
                        stringResource(R.string.ankunft),
                        stringResource(R.string.ankunft_uhr, arrival.toTimeString(), it.durationSeconds.toMinutesString()),
                        Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun LiveSectionPreview() {
    CareConnectTheme(darkTheme = false) {
        LiveSection(
            showLiveLocation = null,
            employeeCoordinate = MockData.employeeLocation,
            homeCoordinate = MockData.patientLocation,
            route = null,
            onAllow = {},
            onDeny = {}
        )
    }
}
