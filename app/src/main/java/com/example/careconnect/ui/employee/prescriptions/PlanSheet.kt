package com.example.careconnect.ui.employee.prescriptions

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.Prescription
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.DateTimePickerField
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.DateHelper.currentTimeMillis
import com.example.careconnect.util.DateHelper.recurringDates
import com.example.careconnect.util.ResourceHelper

private val weekdayLabels get() = listOf(ResourceHelper.getString(R.string.wochentag_mo), ResourceHelper.getString(R.string.wochentag_di), ResourceHelper.getString(R.string.wochentag_mi), ResourceHelper.getString(R.string.wochentag_do), ResourceHelper.getString(R.string.wochentag_fr), ResourceHelper.getString(R.string.wochentag_sa), ResourceHelper.getString(R.string.wochentag_so))
/**
 * Einsätze planen: einmalig oder an festen Wochentagen für einige Wochen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanSheet(
    prescription: Prescription,
    onConfirm: (startMillis: Long, weekdays: Set<Int>, weeks: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var start by remember { mutableStateOf(prescription.startDate ?: currentTimeMillis()) }
    var weekdays by remember { mutableStateOf(emptySet<Int>()) }
    var weeks by remember { mutableStateOf(4) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(stringResource(R.string.einsaetze_planen), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "${prescription.title}" + if (prescription.frequency.isNotBlank()) " · ${prescription.frequency}" else "",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            DateTimePickerField(
                label = stringResource(R.string.erster_einsatz),
                millis = start,
                onValueChange = { start = it }
            )

            Text(stringResource(R.string.wiederholen_leer_nur_einmal), style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                weekdayLabels.forEachIndexed { index, label ->
                    val day = index + 1
                    FilterChip(
                        selected = day in weekdays,
                        onClick = { weekdays = if (day in weekdays) weekdays - day else weekdays + day },
                        label = { Text(label) }
                    )
                }
            }

            if (weekdays.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.fuer), modifier = Modifier.weight(1f))
                    OutlinedButton(onClick = { if (weeks > 1) weeks-- }) { Text("–") }
                    Text(stringResource(R.string.wochen, weeks), fontWeight = FontWeight.SemiBold)
                    OutlinedButton(onClick = { if (weeks < 12) weeks++ }) { Text("+") }
                }
                val count = recurringDates(start, weekdays, weeks).size
                Text(stringResource(R.string.auftraege_werden_angelegt, count), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.abbrechen)) }
                Button(onClick = { onConfirm(start, weekdays, weeks) }, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.uebernehmen))
                }
            }
        }
    }
}

@Preview
@Composable
fun PlanSheetPreview() {
    CareConnectTheme(darkTheme = false) {
        PlanSheet(
            prescription = MockData.prescriptions.first(),
            onConfirm = { _, _, _ -> },
            onDismiss = {}
        )
    }
}
