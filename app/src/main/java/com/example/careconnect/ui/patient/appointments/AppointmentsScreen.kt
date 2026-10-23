package com.example.careconnect.ui.patient.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
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
import com.example.careconnect.domain.model.Appointment
import com.example.careconnect.mock.MockData
import com.example.careconnect.mock.repository.AppointmentRepositoryMock
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.GeoRepositoryMock
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.EmptyStateView
import com.example.careconnect.ui.theme.CareConnectTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    viewModel: AppointmentsViewModel = koinViewModel()
) {
    val appointments by viewModel.appointments.collectAsState()
    var showEditSheet by remember { mutableStateOf(false) }
    var selectedAppointment by remember { mutableStateOf<Appointment?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareTopBar(
            title = stringResource(R.string.termine),
            actions = {
                IconButton(onClick = {
                    viewModel.startEditing(null)
                    showEditSheet = true
                }) {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = stringResource(R.string.termin_hinzufuegen),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        if (appointments.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.EventBusy,
                title = stringResource(R.string.keine_termine),
                message = stringResource(R.string.termin_hinzufuegen_hinweis)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(appointments, key = { it.id }) { appointment ->
                    AppointmentRow(
                        appointment = appointment,
                        onClick = { selectedAppointment = appointment },
                        onEdit = {
                            viewModel.startEditing(appointment)
                            showEditSheet = true
                        },
                        onDelete = { viewModel.delete(appointment) }
                    )
                }
            }
        }
    }

    if (showEditSheet) {
        AddEditAppointmentSheet(
            viewModel = viewModel,
            onDismiss = { showEditSheet = false }
        )
    }

    selectedAppointment?.let { appointment ->
        ModalBottomSheet(onDismissRequest = { selectedAppointment = null }) {
            AppointmentDetails(appointment)
        }
    }
}

@Preview
@Composable
fun AppointmentsScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        AppointmentsScreen(
            viewModel = AppointmentsViewModel(
                AppointmentRepositoryMock(),
                GeoRepositoryMock(),
                AuthRepositoryMock(MockData.patient.id)
            )
        )
    }
}
