package com.example.careconnect.ui.employee.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.color
import com.example.careconnect.util.DateHelper.toTimeString

// Einsätze für heute in zeitlicher Reihenfolge
@Composable
fun TodayTourCard(
    stops: List<TourStop>,
    onStopClick: (String) -> Unit,
    onRouteClick: () -> Unit
) {
    SectionCard(title = stringResource(R.string.heute_fuer_dich), icon = Icons.Default.Route) {
        if (stops.isEmpty()) {
            Text(stringResource(R.string.heute_stehen_keine_einsaetze), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        stops.forEach { stop ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onStopClick(stop.order.id) }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    stop.order.dueDate?.toTimeString() ?: "--:--",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Column(Modifier.weight(1f)) {
                    Text(stop.order.patientName.ifEmpty { stringResource(R.string.patient) }, fontWeight = FontWeight.SemiBold)
                    Text(
                        stop.address.ifEmpty { stop.order.description },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    stop.order.state.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = stop.order.state.color
                )
            }
        }
        if (stops.any { it.address.isNotBlank() }) {
            OutlinedButton(onClick = onRouteClick, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Map, contentDescription = null)
                Text(stringResource(R.string.tour_google_maps))
            }
        }
    }
}

@Preview
@Composable
fun TodayTourCardPreview() {
    CareConnectTheme(darkTheme = false) {
        TodayTourCard(
            stops = MockData.orders.take(2).map { TourStop(it, MockData.patient.fullAddress) },
            onStopClick = {},
            onRouteClick = {}
        )
    }
}
