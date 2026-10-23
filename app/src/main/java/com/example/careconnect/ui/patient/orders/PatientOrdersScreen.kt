package com.example.careconnect.ui.patient.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.mock.MockData
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.EmployeeRepositoryMock
import com.example.careconnect.mock.repository.OrderRepositoryMock
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.theme.CareConnectTheme
import org.koin.androidx.compose.koinViewModel

// Liste wie bei Lieferdiensten: oben der laufende Auftrag, darunter offen und erledigt
@Composable
fun PatientOrdersScreen(
    onOrderClick: (String) -> Unit,
    viewModel: PatientOrdersViewModel = koinViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val activeOrder by viewModel.activeOrder.collectAsState()
    val activeEmployee by viewModel.activeEmployee.collectAsState()
    val openOrders by viewModel.openOrders.collectAsState()
    val doneOrders by viewModel.doneOrders.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareTopBar(title = stringResource(R.string.meine_auftraege))

        when {
            isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            activeOrder == null && openOrders.isEmpty() && doneOrders.isEmpty() -> EmptyOrders()

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                activeOrder?.let { order ->
                    item {
                        ActiveOrderCard(
                            order = order,
                            employeeName = activeEmployee?.name?.ifBlank { null },
                            onClick = { onOrderClick(order.id) }
                        )
                    }
                }

                item { SectionTitle(stringResource(R.string.offen), openOrders.size) }
                if (openOrders.isEmpty()) {
                    item { EmptyHint(stringResource(R.string.keine_offenen_auftraege)) }
                } else {
                    item {
                        OrderGroup(orders = openOrders, onOrderClick = onOrderClick)
                    }
                }

                item { SectionTitle(stringResource(R.string.erledigt), doneOrders.size) }
                if (doneOrders.isEmpty()) {
                    item { EmptyHint(stringResource(R.string.keine_erledigten_auftraege)) }
                } else {
                    item {
                        OrderGroup(orders = doneOrders, onOrderClick = onOrderClick)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PatientOrdersScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        PatientOrdersScreen(
            onOrderClick = {},
            viewModel = PatientOrdersViewModel(
                OrderRepositoryMock(),
                EmployeeRepositoryMock(),
                AuthRepositoryMock(MockData.patient.id)
            )
        )
    }
}
