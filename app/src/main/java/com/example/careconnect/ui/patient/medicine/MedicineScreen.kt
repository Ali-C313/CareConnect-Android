package com.example.careconnect.ui.patient.medicine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.careconnect.domain.model.TimeOfDay
import com.example.careconnect.mock.MockData
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.MedicineRepositoryMock
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.ErrorDialog
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusPurple
import com.example.careconnect.ui.theme.StatusRed
import org.koin.androidx.compose.koinViewModel

@Composable
fun MedicineScreen(
    viewModel: MedicineViewModel = koinViewModel()
) {
    val medicines by viewModel.medicines.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareTopBar(
            title = stringResource(R.string.medikamente),
            actions = {
                IconButton(onClick = { showAddSheet = true }) {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = stringResource(R.string.medikament_hinzufuegen),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        // Sortiert nach Morgens / Mittags / Abends
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TimeOfDay.entries.forEach { time ->
                item(key = time.name) {
                    val filtered = medicines.filter { it.timeOfDay == time }
                    SectionCard(title = time.displayName) {
                        if (filtered.isEmpty()) {
                            Text(
                                stringResource(R.string.keine_medikamente_eingetragen),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            filtered.forEach { medicine ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Medication, contentDescription = null, tint = StatusPurple)
                                    Text(
                                        text = "  ${medicine.name} – ${medicine.dosage}",
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(onClick = { viewModel.deleteMedicine(medicine) }) {
                                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.loeschen), tint = StatusRed)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddSheet) {
        AddMedicineSheet(
            onDismiss = { showAddSheet = false },
            onSave = viewModel::addMedicine
        )
    }

    ErrorDialog(message = errorMessage, onDismiss = viewModel::dismissError)
}

@Preview
@Composable
fun MedicineScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        MedicineScreen(
            viewModel = MedicineViewModel(MedicineRepositoryMock(), AuthRepositoryMock(MockData.patient.id))
        )
    }
}
