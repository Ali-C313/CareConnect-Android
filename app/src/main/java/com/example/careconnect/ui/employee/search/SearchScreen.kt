package com.example.careconnect.ui.employee.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.mock.repository.EmployeeRepositoryMock
import com.example.careconnect.mock.repository.OrderRepositoryMock
import com.example.careconnect.mock.repository.PatientRepositoryMock
import com.example.careconnect.ui.components.AvatarView
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.ResourceHelper
import com.example.careconnect.util.StringHelper
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onEmployeeClick: (String) -> Unit,
    onPatientClick: (String) -> Unit,
    onOrderClick: (String) -> Unit,
    viewModel: SearchViewModel = koinViewModel()
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareTopBar(title = stringResource(R.string.suche), onBack = onBack)

        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onQueryChange,
            placeholder = { Text(stringResource(R.string.suche_mitarbeiter_patienten_auftraege)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { SectionTitle(stringResource(R.string.mitarbeiter)) }
            items(results.employees, key = { "e_${it.id}" }) { employee ->
                SectionCard(modifier = Modifier.clickable { onEmployeeClick(employee.id) }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AvatarView(
                            initials = StringHelper.initials(employee.name),
                            imageUrl = employee.profileImageUrl,
                            size = 32.dp
                        )
                        Column {
                            Text(employee.name, fontWeight = FontWeight.Bold)
                            Text(
                                ResourceHelper.translate(employee.role),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item { SectionTitle(stringResource(R.string.patienten)) }
            items(results.patients, key = { "p_${it.id}" }) { patient ->
                ResultRow(
                    icon = Icons.Default.Person,
                    title = patient.name,
                    subtitle = patient.address,
                    onClick = { onPatientClick(patient.id) }
                )
            }

            item { SectionTitle(stringResource(R.string.auftraege)) }
            items(results.orders, key = { "o_${it.id}" }) { order ->
                ResultRow(
                    icon = Icons.Default.Description,
                    title = stringResource(R.string.auftrag_patient, order.patientName.ifEmpty { stringResource(R.string.unbekannt) }),
                    subtitle = order.description,
                    onClick = { onOrderClick(order.id) }
                )
            }
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        SearchScreen(
            onBack = {},
            onEmployeeClick = {},
            onPatientClick = {},
            onOrderClick = {},
            viewModel = SearchViewModel(
                EmployeeRepositoryMock(),
                PatientRepositoryMock(),
                OrderRepositoryMock()
            )
        )
    }
}
