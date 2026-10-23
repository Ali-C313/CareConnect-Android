package com.example.careconnect.ui.patient.appointments

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.careconnect.mock.MockData
import com.example.careconnect.mock.repository.AppointmentRepositoryMock
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.GeoRepositoryMock
import com.example.careconnect.ui.components.AddressSearchField
import com.example.careconnect.ui.components.CareTextField
import com.example.careconnect.ui.components.DateTimePickerField
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.ResourceHelper

// get(), damit die Liste beim Sprachwechsel neu übersetzt wird
private val defaultBringItems get() = listOf(ResourceHelper.getString(R.string.ausweis), ResourceHelper.getString(R.string.krankenkassenkarte), ResourceHelper.getString(R.string.ueberweisung), ResourceHelper.getString(R.string.bericht))
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAppointmentSheet(
    viewModel: AppointmentsViewModel,
    onDismiss: () -> Unit
) {
    val form by viewModel.form.collectAsState()
    var itemMenuExpanded by remember { mutableStateOf(false) }
    var showCustomItemDialog by remember { mutableStateOf(false) }
    var customItem by remember { mutableStateOf("") }

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
            Text(
                text = if (form.editingId != null) stringResource(R.string.termin_bearbeiten) else stringResource(R.string.termin_hinzufuegen),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Termin Details
            CareTextField(stringResource(R.string.wo_was_beispiel), form.what, viewModel::onWhatChange, icon = Icons.Default.Edit)
            AddressSearchField(
                value = form.address,
                onValueChange = viewModel::onAddressChange,
                suggestions = form.addressSuggestions,
                onSuggestionClick = viewModel::onSuggestionClick
            )
            DateTimePickerField(
                label = stringResource(R.string.datum_uhrzeit),
                millis = form.date,
                onValueChange = viewModel::onDateChange
            )

            // Mitbringen
            Text(stringResource(R.string.mitbringen_optional), style = MaterialTheme.typography.labelLarge)
            Box {
                AssistChip(
                    onClick = { itemMenuExpanded = true },
                    label = { Text(stringResource(R.string.gegenstand_hinzufuegen)) },
                    leadingIcon = { Icon(Icons.Default.ShoppingBag, contentDescription = null) }
                )
                DropdownMenu(
                    expanded = itemMenuExpanded,
                    onDismissRequest = { itemMenuExpanded = false }
                ) {
                    defaultBringItems.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                viewModel.addBringItem(item)
                                itemMenuExpanded = false
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.anderes_hinzufuegen)) },
                        onClick = {
                            itemMenuExpanded = false
                            showCustomItemDialog = true
                        }
                    )
                }
            }

            if (form.bringItems.isNotEmpty()) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    form.bringItems.forEach { item ->
                        InputChip(
                            selected = true,
                            onClick = { viewModel.removeBringItem(item) },
                            label = { Text(item) },
                            trailingIcon = { Icon(Icons.Default.Close, contentDescription = stringResource(R.string.entfernen)) }
                        )
                    }
                }
                Text(
                    stringResource(R.string.entfernen_tippen),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            CareTextField(
                stringResource(R.string.bemerkungen_optional),
                form.notes,
                viewModel::onNotesChange,
                icon = Icons.AutoMirrored.Filled.Notes,
                singleLine = false
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.abbrechen))
                }
                Button(
                    onClick = {
                        viewModel.save()
                        onDismiss()
                    },
                    enabled = form.canSave,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.speichern))
                }
            }
        }
    }

    if (showCustomItemDialog) {
        AlertDialog(
            onDismissRequest = { showCustomItemDialog = false },
            title = { Text(stringResource(R.string.neuen_gegenstand_hinzufuegen)) },
            text = { CareTextField(stringResource(R.string.gegenstand), customItem, { customItem = it }) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.addBringItem(customItem)
                    customItem = ""
                    showCustomItemDialog = false
                }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text(stringResource(R.string.hinzufuegen))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    customItem = ""
                    showCustomItemDialog = false
                }) { Text(stringResource(R.string.abbrechen)) }
            }
        )
    }
}

@Preview
@Composable
fun AddEditAppointmentSheetPreview() {
    CareConnectTheme(darkTheme = false) {
        AddEditAppointmentSheet(
            viewModel = AppointmentsViewModel(
                AppointmentRepositoryMock(),
                GeoRepositoryMock(),
                AuthRepositoryMock(MockData.patient.id)
            ),
            onDismiss = {}
        )
    }
}
