package com.example.careconnect.ui.employee.orderDetail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.R
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.color
import com.example.careconnect.util.DateHelper.toAge
import com.example.careconnect.util.DateHelper.toDateString

/** Kopf mit Patient und Auftrag */
@Composable
fun HeaderCard(
    order: Order,
    patient: Patient?
) {
    SectionCard {
        Text(
            text = stringResource(R.string.patientenauftrag),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = order.patientName.ifEmpty { stringResource(R.string.unbekannter_patient) },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        if (order.description.isNotEmpty()) {
            Text(
                text = order.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        patient?.birthDate?.let { birthDate ->
            Text(
                text = stringResource(R.string.geboren_jahre, birthDate.toDateString(), birthDate.toAge()),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (order.notes.isNotEmpty()) {
            InfoRow(Icons.Default.EditNote, stringResource(R.string.bemerkungen), order.notes.joinToString(", "))
        }
    }
}

@Preview
@Composable
fun HeaderCardPreview() {
    CareConnectTheme(darkTheme = false) {
        HeaderCard(order = MockData.orders.first(), patient = MockData.patient)
    }
}
