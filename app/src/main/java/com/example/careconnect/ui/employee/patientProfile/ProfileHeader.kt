package com.example.careconnect.ui.employee.patientProfile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.mock.MockData
import com.example.careconnect.util.MapsHelper.mapsSearchUrl
import com.example.careconnect.ui.components.AvatarView
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.StringHelper
import com.example.careconnect.util.UriHelper.openSafely

@Composable
fun ProfileHeader(
    patient: Patient,
    onEmergencyClick: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    SectionCard {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AvatarView(
                initials = StringHelper.initials(patient.name),
                imageUrl = patient.profileImageUrl,
                size = 64.dp
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = patient.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                // Adresse antippen -> Karten-App
                if (patient.fullAddress.isNotEmpty()) {
                    Row(
                        modifier = Modifier.clickable {
                            uriHandler.openSafely(mapsSearchUrl(patient.fullAddress))
                        },
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(patient.fullAddress, color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    Text(
                        stringResource(R.string.keine_adresse_angegeben),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            TextButton(onClick = onEmergencyClick) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ContactPhone, contentDescription = stringResource(R.string.notfallkontakt_anzeigen))
                    Text(stringResource(R.string.notfall), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Preview
@Composable
fun ProfileHeaderPreview() {
    CareConnectTheme(darkTheme = false) {
        ProfileHeader(patient = MockData.patient, onEmergencyClick = {})
    }
}
