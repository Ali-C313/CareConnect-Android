package com.example.careconnect.ui.employee.patientProfile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.EmergencyContact
import com.example.careconnect.mock.MockData
import com.example.careconnect.util.MapsHelper.mapsSearchUrl
import com.example.careconnect.ui.components.InfoButtonRow
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.StringHelper
import com.example.careconnect.util.UriHelper.openSafely

/**
 * Inhalt des Sheets "Notfallkontakt". Ohne Kontakt gibt es einen Button zum Hinzufügen.
 */
@Composable
fun EmergencyContactDetails(
    contact: EmergencyContact?,
    onAddClick: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.notfallkontakt),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        if (contact == null || contact.name.isBlank()) {
            Text(stringResource(R.string.kein_notfallkontakt_vorhanden))
            Button(onClick = onAddClick, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.AddCircle, contentDescription = null)
                Text(stringResource(R.string.notfallkontakt_hinzufuegen_button))
            }
        } else {
            SectionCard {
                InfoRow(Icons.Default.Person, stringResource(R.string.name), contact.name)
                if (contact.relation.isNotEmpty()) {
                    InfoRow(Icons.Default.Favorite, stringResource(R.string.beziehung), contact.relation)
                }
                contact.age?.let { InfoRow(Icons.Default.Cake, stringResource(R.string.alter), "$it") }

                if (contact.phone.isNotEmpty()) {
                    InfoButtonRow(
                        icon = Icons.Default.Phone,
                        title = stringResource(R.string.telefon),
                        value = contact.phone,
                        actionIcon = Icons.Default.Call,
                        actionDescription = stringResource(R.string.anrufen),
                        onClick = { uriHandler.openSafely("tel:${StringHelper.formattedPhone(contact.phone)}") }
                    )
                } else {
                    InfoRow(Icons.Default.Phone, stringResource(R.string.telefon), stringResource(R.string.keine_telefonnummer_angegeben))
                }

                if (contact.email.isNotEmpty()) {
                    InfoButtonRow(
                        icon = Icons.Default.Email,
                        title = stringResource(R.string.e_mail),
                        value = contact.email,
                        actionIcon = Icons.AutoMirrored.Filled.Send,
                        actionDescription = stringResource(R.string.e_mail_senden),
                        onClick = { uriHandler.openSafely("mailto:${contact.email}") }
                    )
                } else {
                    InfoRow(Icons.Default.Email, stringResource(R.string.e_mail), stringResource(R.string.keine_e_mail_adresse_angegeben))
                }

                if (contact.address.isNotEmpty()) {
                    InfoButtonRow(
                        icon = Icons.Default.LocationOn,
                        title = stringResource(R.string.adresse),
                        value = contact.address,
                        actionIcon = Icons.Default.Map,
                        actionDescription = stringResource(R.string.karten_oeffnen),
                        onClick = { uriHandler.openSafely(mapsSearchUrl(contact.address)) }
                    )
                } else {
                    InfoRow(Icons.Default.LocationOn, stringResource(R.string.adresse), stringResource(R.string.keine_adresse_angegeben))
                }
            }
            OutlinedButton(onClick = onAddClick, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Text(stringResource(R.string.bearbeiten_button))
            }
        }
    }
}

@Preview
@Composable
fun EmergencyContactDetailsPreview() {
    CareConnectTheme(darkTheme = false) {
        EmergencyContactDetails(contact = MockData.patient.emergencyContact, onAddClick = {})
    }
}
