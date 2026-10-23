package com.example.careconnect.ui.patient.medicine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.TimeOfDay
import com.example.careconnect.ui.components.CareTextField
import com.example.careconnect.ui.components.FilterTabs
import com.example.careconnect.ui.theme.CareConnectTheme

/**
 * Sheet "Medikament hinzufügen".
 * @param onSave Name, Dosierung, Tageszeit
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineSheet(
    onDismiss: () -> Unit,
    onSave: (String, String, TimeOfDay) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf(TimeOfDay.MORNING) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.medikament_hinzufuegen),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            CareTextField(stringResource(R.string.name), name, { name = it }, icon = Icons.Default.Medication)
            CareTextField(stringResource(R.string.dosierung), dosage, { dosage = it }, icon = Icons.Default.LocalPharmacy)

            Text(stringResource(R.string.einnahmezeit), style = MaterialTheme.typography.labelLarge)
            FilterTabs(
                options = TimeOfDay.entries,
                selected = selectedTime,
                label = { it.displayName },
                onSelect = { selectedTime = it }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.abbrechen))
                }
                Button(
                    onClick = {
                        onSave(name, dosage, selectedTime)
                        onDismiss()
                    },
                    enabled = name.isNotBlank() && dosage.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.speichern))
                }
            }
        }
    }
}

@Preview
@Composable
fun AddMedicineSheetPreview() {
    CareConnectTheme(darkTheme = false) {
        AddMedicineSheet(onDismiss = {}, onSave = { _, _, _ -> })
    }
}
