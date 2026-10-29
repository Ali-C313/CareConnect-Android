package com.example.careconnect.ui.employee.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.domain.model.StaffStatus
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.EmployeeRepositoryMock
import com.example.careconnect.mock.repository.OrderRepositoryMock
import com.example.careconnect.mock.repository.PatientRepositoryMock
import com.example.careconnect.util.MapsHelper.tourUrl
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.StatTile
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusBlue
import com.example.careconnect.ui.theme.StatusGreen
import com.example.careconnect.ui.theme.StatusOrange
import com.example.careconnect.ui.theme.color
import com.example.careconnect.util.UriHelper.openSafely
import org.koin.androidx.compose.koinViewModel

/** Startseite der Mitarbeiter mit Status, Tour und Team */
@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onEmployeeClick: (String) -> Unit,
    onOrderClick: (String) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val employees by viewModel.employees.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val currentEmployee by viewModel.currentEmployee.collectAsState()
    val todayTour by viewModel.todayTour.collectAsState()
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareTopBar(title = stringResource(R.string.home))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                HeaderSection(
                    status = currentEmployee?.status ?: StaffStatus.AVAILABLE,
                    onStatusChange = viewModel::updateStatus,
                    onSearchClick = onSearchClick
                )
            }

            item {
                Text(
                    text = stringResource(R.string.status_aendern_hinweis),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatTile(
                        value = orders.count { it.state == OrderState.OFFEN },
                        label = stringResource(R.string.offen),
                        icon = Icons.Default.Schedule,
                        color = StatusOrange,
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        value = orders.count { it.state == OrderState.AKTUELL },
                        label = stringResource(R.string.aktuell),
                        icon = Icons.Default.PlayCircle,
                        color = StatusBlue,
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        value = orders.count { it.state == OrderState.ERLEDIGT },
                        label = stringResource(R.string.erledigt),
                        icon = Icons.Default.CheckCircle,
                        color = StatusGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                TodayTourCard(
                    stops = todayTour,
                    onStopClick = onOrderClick,
                    onRouteClick = {
                        tourUrl(todayTour.map { it.address }.filter { it.isNotBlank() })
                            ?.let { uriHandler.openSafely(it) }
                    }
                )
            }
            item {
                Text(
                    text = stringResource(R.string.mitarbeiter_ueberschrift),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(employees, key = { it.id }) { employee ->
                EmployeeRow(
                    employee = employee,
                    onClick = { onEmployeeClick(employee.id) }
                )
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        HomeScreen(
            onSearchClick = {},
            onEmployeeClick = {},
            onOrderClick = {},
            viewModel = HomeViewModel(
                EmployeeRepositoryMock(),
                OrderRepositoryMock(),
                AuthRepositoryMock(),
                PatientRepositoryMock()
            )
        )
    }
}
