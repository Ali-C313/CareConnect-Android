package com.example.careconnect.ui.employee.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.Order
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.OrderRepositoryMock
import com.example.careconnect.mock.repository.PatientRepositoryMock
import com.example.careconnect.ui.components.DateTimePickerField
import com.example.careconnect.ui.theme.CareConnectTheme
import org.koin.androidx.compose.koinViewModel

/**
 * Sheet zum Erstellen oder Bearbeiten eines Auftrags.
 * @param existingOrder null = neuer Auftrag
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderSheet(
    existingOrder: Order?,
    onDismiss: () -> Unit,
    viewModel: NewOrderViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val patients by viewModel.patients.collectAsState()
    var patientMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(existingOrder) {
        viewModel.start(existingOrder)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (uiState.isEditing) stringResource(R.string.auftrag_bearbeiten) else stringResource(R.string.neuer_auftrag),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Patient auswählen
            Text(stringResource(R.string.patient_auswaehlen), style = MaterialTheme.typography.labelLarge)
            Box {
                val selectedName = patients.firstOrNull { it.id == uiState.selectedPatientId }?.name
                Surface(
                    onClick = { patientMenuExpanded = true },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = selectedName ?: stringResource(R.string.patient_waehlen),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
                DropdownMenu(
                    expanded = patientMenuExpanded,
                    onDismissRequest = { patientMenuExpanded = false }
                ) {
                    patients.forEach { patient ->
                        DropdownMenuItem(
                            text = { Text(patient.name) },
                            onClick = {
                                viewModel.onPatientSelect(patient.id)
                                patientMenuExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text(stringResource(R.string.beschreibung_des_auftrags)) },
                minLines = 3,
                maxLines = 6,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            DateTimePickerField(
                label = stringResource(R.string.ausfuehrungszeit),
                millis = uiState.dueDate,
                onValueChange = viewModel::onDueDateChange
            )

            uiState.errorMessage?.let { Text(it, color = Color.Red) }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.abbrechen))
                }
                Button(
                    onClick = { viewModel.save(onSuccess = onDismiss) },
                    enabled = uiState.canSave,
                    modifier = Modifier.weight(1f)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text(stringResource(R.string.speichern))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun NewOrderSheetPreview() {
    CareConnectTheme(darkTheme = false) {
        NewOrderSheet(
            existingOrder = null,
            onDismiss = {},
            viewModel = NewOrderViewModel(OrderRepositoryMock(), AuthRepositoryMock(), PatientRepositoryMock())
        )
    }
}
