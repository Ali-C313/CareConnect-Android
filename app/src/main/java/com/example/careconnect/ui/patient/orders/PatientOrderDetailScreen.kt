package com.example.careconnect.ui.patient.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.example.careconnect.R
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.mock.MockData
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.EmployeeRepositoryMock
import com.example.careconnect.mock.repository.GeoRepositoryMock
import com.example.careconnect.mock.repository.LocationRepositoryMock
import com.example.careconnect.mock.repository.OrderRepositoryMock
import com.example.careconnect.mock.repository.PatientRepositoryMock
import com.example.careconnect.mock.repository.SettingsRepositoryMock
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.components.SignatureView
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.DateHelper.toDateTimeString
import org.koin.androidx.compose.koinViewModel

@Composable
fun PatientOrderDetailScreen(
    onBack: () -> Unit,
    viewModel: PatientOrderDetailViewModel = koinViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val order by viewModel.order.collectAsState()
    val employee by viewModel.employee.collectAsState()
    val showLiveLocation by viewModel.showLiveLocation.collectAsState()
    val employeeCoordinate by viewModel.employeeCoordinate.collectAsState()
    val homeCoordinate by viewModel.homeCoordinate.collectAsState()
    val route by viewModel.route.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareTopBar(title = stringResource(R.string.auftragsdetails), onBack = onBack)

        val current = order
        when {
            isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            current == null -> Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.dieser_auftrag_existiert_nicht_mehr), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OrderHeaderCard(current)

                if (current.state == OrderState.AKTUELL) {
                    LiveSection(
                        showLiveLocation = showLiveLocation,
                        employeeCoordinate = employeeCoordinate,
                        homeCoordinate = homeCoordinate,
                        route = route,
                        onAllow = { viewModel.setShowLiveLocation(true) },
                        onDeny = { viewModel.setShowLiveLocation(false) }
                    )
                }

                StatusTimeline(current)

                if (current.employeeId.isNotEmpty()) {
                    EmployeeSection(employee)
                }

                current.report?.let { report ->
                    SectionCard(title = stringResource(R.string.leistungsnachweis), icon = Icons.AutoMirrored.Filled.Notes) {
                        if (report.note.isNotBlank()) Text(report.note)
                        val info = listOfNotNull(
                            report.completedByName.ifBlank { null }?.let { stringResource(R.string.erledigt_von, it) },
                            report.completedAt?.toDateTimeString()
                        ).joinToString(" · ")
                        if (info.isNotEmpty()) {
                            Text(
                                info,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (report.signature.isNotBlank()) {
                            Text(stringResource(R.string.unterschrift), style = MaterialTheme.typography.labelMedium)
                            SignatureView(signature = report.signature, modifier = Modifier.fillMaxWidth())
                            if (report.signedBy.isNotBlank()) {
                                Text(report.signedBy, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PatientOrderDetailScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        PatientOrderDetailScreen(
            onBack = {},
            viewModel = PatientOrderDetailViewModel(
                OrderRepositoryMock(),
                EmployeeRepositoryMock(),
                LocationRepositoryMock(),
                PatientRepositoryMock(),
                GeoRepositoryMock(),
                AuthRepositoryMock(MockData.patient.id),
                SettingsRepositoryMock(),
                SavedStateHandle(mapOf("orderId" to "o1"))
            )
        )
    }
}
