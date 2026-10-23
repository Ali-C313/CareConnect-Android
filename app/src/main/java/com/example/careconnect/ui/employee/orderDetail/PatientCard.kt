package com.example.careconnect.ui.employee.orderDetail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.R
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.mock.MockData
import com.example.careconnect.platform.mapsSearchUrl
import com.example.careconnect.ui.components.InfoButtonRow
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusOrange
import com.example.careconnect.ui.theme.StatusPurple
import com.example.careconnect.ui.theme.StatusRed
import com.example.careconnect.ui.theme.icon
import com.example.careconnect.util.StringHelper
import com.example.careconnect.util.UriHelper.openSafely

@Composable
fun PatientCard(patient: Patient) {
    val uriHandler = LocalUriHandler.current

    SectionCard(title = stringResource(R.string.patientendaten), icon = Icons.Default.Person) {
        Text(
            text = patient.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        if (patient.phone.isNotEmpty()) {
            InfoButtonRow(
                icon = Icons.Default.Phone,
                title = stringResource(R.string.telefon),
                value = patient.phone,
                actionIcon = Icons.Default.Call,
                actionDescription = stringResource(R.string.anrufen),
                onClick = { uriHandler.openSafely("tel:${StringHelper.formattedPhone(patient.phone)}") }
            )
        }
        if (patient.email.isNotEmpty()) {
            InfoButtonRow(
                icon = Icons.Default.Email,
                title = stringResource(R.string.e_mail),
                value = patient.email,
                actionIcon = Icons.AutoMirrored.Filled.Send,
                actionDescription = stringResource(R.string.e_mail_senden),
                onClick = { uriHandler.openSafely("mailto:${patient.email}") }
            )
        }
        if (patient.fullAddress.isNotEmpty()) {
            InfoButtonRow(
                icon = Icons.Default.Map,
                title = stringResource(R.string.adresse),
                value = patient.fullAddress,
                actionIcon = Icons.Default.Navigation,
                actionDescription = stringResource(R.string.karten_oeffnen),
                onClick = { uriHandler.openSafely(mapsSearchUrl(patient.fullAddress)) }
            )
        }
        if (patient.medications.isNotEmpty()) {
            SmallSection(Icons.Default.Medication, StatusPurple, stringResource(R.string.medikamente), patient.medications.joinToString("\n") { "• ${it.name}" })
        }
        if (patient.allergies.isNotBlank()) {
            SmallSection(Icons.Default.Warning, StatusOrange, stringResource(R.string.allergien), patient.allergies)
        }
        if (patient.medicalHistory.isNotBlank()) {
            SmallSection(Icons.Default.Warning, StatusRed, stringResource(R.string.medizinische_vorgeschichte), patient.medicalHistory)
        }
        if (patient.history.isNotEmpty()) {
            SmallSection(Icons.Default.Schedule, MaterialTheme.colorScheme.onSurfaceVariant, stringResource(R.string.historie), patient.history.joinToString("\n") { "• $it" })
        }
    }
}

@Preview
@Composable
fun PatientCardPreview() {
    CareConnectTheme(darkTheme = false) {
        PatientCard(patient = MockData.patient)
    }
}
