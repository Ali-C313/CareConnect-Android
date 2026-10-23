package com.example.careconnect.ui.employee.orderDetail

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.R
import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.MapMarker
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.domain.model.RouteInfo
import com.example.careconnect.mock.MockData
import com.example.careconnect.platform.mapsDirectionsUrl
import com.example.careconnect.ui.components.CareMap
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.icon
import com.example.careconnect.util.DateHelper.currentTimeMillis
import com.example.careconnect.util.DateHelper.toTimeString
import com.example.careconnect.util.ResourceHelper
import com.example.careconnect.util.StringHelper.toKmString
import com.example.careconnect.util.StringHelper.toMinutesString
import com.example.careconnect.util.UriHelper.openSafely

/**
 * Live-Karte mit Mitarbeiter (blau) und Patient (rot), dazu ETA.
 */
@Composable
fun LiveMapCard(
    employeeCoordinate: Coordinate?,
    patientCoordinate: Coordinate?,
    route: RouteInfo?
) {
    val uriHandler = LocalUriHandler.current
    val markers = buildList {
        patientCoordinate?.let { add(MapMarker(ResourceHelper.getString(R.string.patient), it)) }
        employeeCoordinate?.let { add(MapMarker(ResourceHelper.getString(R.string.mitarbeiter), it, isEmployee = true)) }
    }

    SectionCard(title = stringResource(R.string.live_karte), icon = Icons.Default.Map) {
        CareMap(markers = markers, route = route?.path ?: emptyList())

        route?.let {
            val arrival = currentTimeMillis() + (it.durationSeconds * 1000).toLong()
            InfoRow(
                Icons.Default.DirectionsCar,
                stringResource(R.string.voraussichtliche_ankunft),
                stringResource(R.string.ankunft_uhr_lang, arrival.toTimeString(), it.durationSeconds.toMinutesString(), it.distanceMeters.toKmString())
            )
        }

        patientCoordinate?.let { target ->
            Button(
                onClick = { uriHandler.openSafely(mapsDirectionsUrl(target.latitude, target.longitude)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Navigation, contentDescription = null)
                Text(stringResource(R.string.navigation_oeffnen))
            }
        }
    }
}

@Preview
@Composable
fun LiveMapCardPreview() {
    CareConnectTheme(darkTheme = false) {
        LiveMapCard(
            employeeCoordinate = MockData.employeeLocation,
            patientCoordinate = MockData.patientLocation,
            route = RouteInfo(1_400.0, 300.0, listOf(MockData.employeeLocation, MockData.patientLocation))
        )
    }
}
