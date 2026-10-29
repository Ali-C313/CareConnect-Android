package com.example.careconnect.ui.employee.orderDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.example.careconnect.R
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.EmployeeRepositoryMock
import com.example.careconnect.mock.repository.GeoRepositoryMock
import com.example.careconnect.mock.repository.LocationRepositoryMock
import com.example.careconnect.mock.repository.OrderRepositoryMock
import com.example.careconnect.mock.repository.PatientRepositoryMock
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.EmptyStateView
import com.example.careconnect.ui.components.ErrorDialog
import com.example.careconnect.ui.components.LoadingView
import com.example.careconnect.ui.employee.orders.NewOrderSheet
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusRed
import org.koin.androidx.compose.koinViewModel

@Composable
fun OrderDetailScreen(
    onBack: () -> Unit,
    viewModel: OrderDetailViewModel = koinViewModel()
) {
    val order by viewModel.order.collectAsState()
    val notFound by viewModel.notFound.collectAsState()
    val patient by viewModel.patient.collectAsState()
    val assignedEmployee by viewModel.assignedEmployee.collectAsState()
    val employeeCoordinate by viewModel.employeeCoordinate.collectAsState()
    val patientCoordinate by viewModel.patientCoordinate.collectAsState()
    val route by viewModel.route.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showCompleteDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(Modifier.fillMaxSize()) {
            CareTopBar(title = stringResource(R.string.auftragsdetails), onBack = onBack)

            val current = order
            if (current == null && notFound) {
                EmptyStateView(
                    icon = Icons.Default.SearchOff,
                    title = stringResource(R.string.auftrag_nicht_gefunden),
                    message = stringResource(R.string.auftrag_nicht_verfuegbar)
                )
            } else if (current == null) {
                LoadingView(stringResource(R.string.lade_auftrag))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HeaderCard(order = current, patient = patient)
                    StatusCard(order = current, employeeName = assignedEmployee?.name)

                    if (current.state == OrderState.AKTUELL) {
                        LiveMapCard(
                            employeeCoordinate = employeeCoordinate,
                            patientCoordinate = patientCoordinate,
                            route = route
                        )
                    }

                    current.report?.let { ReportCard(it) }

                    patient?.let { PatientCard(it) }

                    ActionCard(
                        state = current.state,
                        isMine = current.employeeId.isNotEmpty() && current.employeeId == viewModel.currentUserId,
                        assignedName = assignedEmployee?.name,
                        onEdit = { showEditSheet = true },
                        onAccept = viewModel::accept,
                        onComplete = { showCompleteDialog = true },
                        onDelete = { showDeleteDialog = true }
                    )
                }
            }
        }

        // Lade-Overlay während einer Aktion
        if (isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

    if (showCompleteDialog) {
        CompletionSheet(
            patientName = patient?.name ?: order?.patientName ?: "",
            onConfirm = { note, signature, signedBy ->
                showCompleteDialog = false
                viewModel.complete(note, signature, signedBy)
            },
            onDismiss = { showCompleteDialog = false }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.auftrag_loeschen)) },
            text = { Text(stringResource(R.string.auftrag_dauerhaft_geloescht)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.delete(onDeleted = onBack)
                }) { Text(stringResource(R.string.loeschen), color = StatusRed) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.abbrechen)) }
            }
        )
    }

    ErrorDialog(message = errorMessage, onDismiss = viewModel::dismissError)

    if (showEditSheet) {
        NewOrderSheet(
            existingOrder = order,
            onDismiss = { showEditSheet = false }
        )
    }
}

@Preview
@Composable
fun OrderDetailScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        OrderDetailScreen(
            onBack = {},
            viewModel = OrderDetailViewModel(
                OrderRepositoryMock(),
                AuthRepositoryMock(),
                PatientRepositoryMock(),
                EmployeeRepositoryMock(),
                LocationRepositoryMock(),
                GeoRepositoryMock(),
                SavedStateHandle(mapOf("orderId" to "o1"))
            )
        )
    }
}
