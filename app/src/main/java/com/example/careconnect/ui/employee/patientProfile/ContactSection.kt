package com.example.careconnect.ui.employee.patientProfile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.R
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.InfoButtonRow
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.StringHelper
import com.example.careconnect.util.UriHelper.openSafely

/** Kontaktdaten des Patienten */
@Composable
fun ContactSection(patient: Patient) {
    val uriHandler = LocalUriHandler.current

    SectionCard(title = stringResource(R.string.kontakt)) {
        if (patient.phone.isNotEmpty()) {
            InfoButtonRow(
                icon = Icons.Default.Phone,
                title = stringResource(R.string.telefon),
                value = patient.phone,
                actionIcon = Icons.Default.Call,
                actionDescription = stringResource(R.string.anrufen),
                onClick = { uriHandler.openSafely("tel:${StringHelper.formattedPhone(patient.phone)}") }
            )
        } else {
            InfoRow(Icons.Default.Phone, stringResource(R.string.telefon), stringResource(R.string.keine_telefonnummer_angegeben))
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
        } else {
            InfoRow(Icons.Default.Email, stringResource(R.string.e_mail), stringResource(R.string.keine_e_mail_adresse_angegeben))
        }
    }
}

@Preview
@Composable
fun ContactSectionPreview() {
    CareConnectTheme(darkTheme = false) {
        ContactSection(patient = MockData.patient)
    }
}
