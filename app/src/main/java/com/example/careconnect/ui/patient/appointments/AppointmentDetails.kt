package com.example.careconnect.ui.patient.appointments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.Appointment
import com.example.careconnect.mock.MockData
import com.example.careconnect.platform.mapsSearchUrl
import com.example.careconnect.ui.components.InfoButtonRow
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.DateHelper.toDateTimeString
import com.example.careconnect.util.UriHelper.openSafely

@Composable
fun AppointmentDetails(appointment: Appointment) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = appointment.what,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        InfoRow(Icons.Default.CalendarMonth, stringResource(R.string.datum), appointment.date.toDateTimeString())

        if (appointment.address.isNotEmpty()) {
            InfoButtonRow(
                icon = Icons.Default.LocationOn,
                title = stringResource(R.string.adresse),
                value = appointment.address,
                actionIcon = Icons.Default.Map,
                actionDescription = stringResource(R.string.karten_oeffnen),
                onClick = { uriHandler.openSafely(mapsSearchUrl(appointment.address)) }
            )
        }

        if (appointment.bringItems.isNotEmpty()) {
            InfoRow(Icons.Default.ShoppingBag, stringResource(R.string.mitbringen), appointment.bringItems.joinToString(", "))
        }

        if (appointment.notes.isNotEmpty()) {
            InfoRow(Icons.AutoMirrored.Filled.Notes, stringResource(R.string.notizen), appointment.notes)
        } else {
            Text(stringResource(R.string.keine_weiteren_informationen), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview
@Composable
fun AppointmentDetailsPreview() {
    CareConnectTheme(darkTheme = false) {
        AppointmentDetails(appointment = MockData.appointments.first())
    }
}
