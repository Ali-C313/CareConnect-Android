package com.example.careconnect.ui.employee.prescriptions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.Prescription
import com.example.careconnect.domain.model.PrescriptionStatus
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusRed
import com.example.careconnect.util.DateHelper.toDateString

@Composable
fun PrescriptionCard(
    prescription: Prescription,
    incoming: Boolean,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onComplete: () -> Unit,
    onWithdraw: () -> Unit
) {
    SectionCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                prescription.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            StatusLabel(prescription.status)
        }

        InfoLine(Icons.Default.Person, prescription.patientName)
        InfoLine(
            Icons.Default.Business,
            if (incoming) stringResource(R.string.von_organisation, prescription.fromOrgName) else stringResource(R.string.an_organisation, prescription.toOrgName)
        )
        if (prescription.frequency.isNotBlank()) {
            InfoLine(Icons.Default.Repeat, prescription.frequency)
        }
        prescription.startDate?.let {
            Text(
                stringResource(R.string.ab_datum, it.toDateString()) + if (prescription.createdByName.isNotBlank()) " · ${prescription.createdByName}" else "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (prescription.details.isNotBlank()) {
            Text(prescription.details, style = MaterialTheme.typography.bodyMedium)
        }

        when {
            incoming && prescription.status == PrescriptionStatus.OFFEN -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onAccept, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.als_auftrag_uebernehmen)) }
                OutlinedButton(onClick = onDecline) { Text(stringResource(R.string.ablehnen)) }
            }
            incoming && prescription.status == PrescriptionStatus.ANGENOMMEN ->
                OutlinedButton(onClick = onComplete, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.als_erledigt_markieren)) }
            !incoming && prescription.status == PrescriptionStatus.OFFEN ->
                TextButton(onClick = onWithdraw, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.zurueckziehen), color = StatusRed) }
        }
    }
}

@Preview
@Composable
fun PrescriptionCardPreview() {
    CareConnectTheme(darkTheme = false) {
        PrescriptionCard(
            prescription = MockData.prescriptions.first(),
            incoming = true,
            onAccept = {},
            onDecline = {},
            onComplete = {},
            onWithdraw = {}
        )
    }
}
