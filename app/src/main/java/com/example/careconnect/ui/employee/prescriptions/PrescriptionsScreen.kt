package com.example.careconnect.ui.employee.prescriptions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.OrganizationType
import com.example.careconnect.domain.model.Prescription
import com.example.careconnect.mock.MockData
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.EmployeeRepositoryMock
import com.example.careconnect.mock.repository.OrderRepositoryMock
import com.example.careconnect.mock.repository.OrganizationRepositoryMock
import com.example.careconnect.mock.repository.PatientRepositoryMock
import com.example.careconnect.mock.repository.PrescriptionRepositoryMock
import com.example.careconnect.session.OrgSession
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.EmptyStateView
import com.example.careconnect.ui.components.FilterTabs
import com.example.careconnect.ui.theme.CareConnectTheme
import org.koin.androidx.compose.koinViewModel

/**
 * Praxis/Klinik: Verordnungen an Pflegedienste schicken.
 * Pflegedienst: eingehende Verordnungen übernehmen.
 */
@Composable
fun PrescriptionsScreen(
    orgType: OrganizationType,
    viewModel: PrescriptionsViewModel = koinViewModel()
) {
    val prescriptions by viewModel.prescriptions.collectAsState()
    val direction by viewModel.direction.collectAsState()
    val message by viewModel.message.collectAsState()
    var showNewSheet by remember { mutableStateOf(false) }
    var planTarget by remember { mutableStateOf<Prescription?>(null) }
    val canPrescribe = orgType != OrganizationType.PFLEGEDIENST

    // Praxis sieht zuerst, was sie verschickt hat
    LaunchedEffect(orgType) {
        if (canPrescribe) viewModel.onDirectionChange(PrescriptionDirection.OUTGOING)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareTopBar(
            title = stringResource(R.string.verordnungen),
            actions = {
                if (canPrescribe) {
                    IconButton(onClick = {
                        viewModel.resetForm()
                        showNewSheet = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.neue_verordnung))
                    }
                }
            }
        )

        FilterTabs(
            options = PrescriptionDirection.entries,
            selected = direction,
            label = { it.displayName },
            onSelect = viewModel::onDirectionChange,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (prescriptions.isEmpty()) {
            EmptyStateView(
                icon = Icons.AutoMirrored.Filled.Assignment,
                title = stringResource(R.string.keine_verordnungen),
                message = if (direction == PrescriptionDirection.INCOMING) {
                    stringResource(R.string.verordnungen_leer_hinweis)
                } else {
                    stringResource(R.string.verordnungen_hinweis)
                }
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
            ) {
                items(prescriptions, key = { it.id }) { prescription ->
                    PrescriptionCard(
                        prescription = prescription,
                        incoming = direction == PrescriptionDirection.INCOMING,
                        onAccept = { planTarget = prescription },
                        onDecline = { viewModel.decline(prescription) },
                        onComplete = { viewModel.complete(prescription) },
                        onWithdraw = { viewModel.withdraw(prescription) }
                    )
                }
            }
        }
    }

    if (showNewSheet) {
        NewPrescriptionSheet(
            viewModel = viewModel,
            onDismiss = { showNewSheet = false }
        )
    }

    planTarget?.let { prescription ->
        PlanSheet(
            prescription = prescription,
            onConfirm = { start, weekdays, weeks ->
                viewModel.accept(prescription, start, weekdays, weeks)
                planTarget = null
            },
            onDismiss = { planTarget = null }
        )
    }

    message?.let { text ->
        AlertDialog(
            onDismissRequest = viewModel::dismissMessage,
            title = { Text(stringResource(R.string.verordnung)) },
            text = { Text(text) },
            confirmButton = { TextButton(onClick = viewModel::dismissMessage) { Text("OK") } }
        )
    }
}

@Preview
@Composable
fun PrescriptionsScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        PrescriptionsScreen(
            orgType = OrganizationType.PFLEGEDIENST,
            viewModel = PrescriptionsViewModel(
                PrescriptionRepositoryMock(),
                OrderRepositoryMock(),
                AuthRepositoryMock(),
                PatientRepositoryMock(),
                EmployeeRepositoryMock(),
                OrganizationRepositoryMock(),
                OrgSession().apply { set(MockData.organization.id) }
            )
        )
    }
}
