package com.example.careconnect.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.DateHelper.combineDateAndTime
import com.example.careconnect.util.DateHelper.hour
import com.example.careconnect.util.DateHelper.minute
import com.example.careconnect.util.DateHelper.toDateString
import com.example.careconnect.util.DateHelper.toDateTimeString
import com.example.careconnect.util.DateHelper.toPickerDateMillis

/**
 * Feld für Datum (und optional Uhrzeit). Öffnet erst den DatePicker, dann den TimePicker.
 * @param millis aktueller Wert in Millisekunden
 * @param withTime false = nur Datum (z.B. Geburtsdatum)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerField(
    label: String,
    millis: Long,
    onValueChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    withTime: Boolean = true
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var pickedDate by remember { mutableStateOf<Long?>(null) }

    Surface(
        onClick = { showDatePicker = true },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (withTime) millis.toDateTimeString() else millis.toDateString(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

    if (showDatePicker) {
        val dateState = rememberDatePickerState(initialSelectedDateMillis = millis.toPickerDateMillis())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    val selected = dateState.selectedDateMillis
                    if (selected != null) {
                        if (withTime) {
                            pickedDate = selected
                            showTimePicker = true
                        } else {
                            // Mittag, damit sich das Datum durch Zeitzonen nicht verschiebt
                            onValueChange(combineDateAndTime(selected, 12, 0))
                        }
                    }
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.abbrechen)) }
            }
        ) {
            DatePicker(state = dateState)
        }
    }

    if (showTimePicker) {
        val timeState = rememberTimePickerState(
            initialHour = millis.hour(),
            initialMinute = millis.minute(),
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showTimePicker = false
                    pickedDate?.let { date ->
                        onValueChange(combineDateAndTime(date, timeState.hour, timeState.minute))
                    }
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text(stringResource(R.string.abbrechen)) }
            },
            text = {
                TimePicker(state = timeState)
            }
        )
    }
}

@Preview
@Composable
fun DateTimePickerFieldPreview() {
    CareConnectTheme(darkTheme = false) {
        DateTimePickerField(
            label = "Datum & Uhrzeit",
            millis = MockData.orders.first().dueDate ?: 0L,
            onValueChange = {}
        )
    }
}
