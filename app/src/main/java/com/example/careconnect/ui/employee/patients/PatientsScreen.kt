package com.example.careconnect.ui.employee.patients

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.mock.repository.PatientRepositoryMock
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.EmptyStateView
import com.example.careconnect.ui.components.LoadingView
import com.example.careconnect.ui.theme.CareConnectTheme
import org.koin.androidx.compose.koinViewModel

/** Liste aller Patienten der Organisation */
@Composable
fun PatientsScreen(
    onPatientClick: (String) -> Unit,
    viewModel: PatientsViewModel = koinViewModel()
) {
    val patients by viewModel.patients.collectAsState()
    val list = patients

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareTopBar(title = stringResource(R.string.patienten))

        when {
            list == null -> LoadingView(stringResource(R.string.lade_patienten))

            list.isEmpty() -> EmptyStateView(
                icon = Icons.Default.PersonSearch,
                title = stringResource(R.string.keine_patienten_gefunden),
                message = stringResource(R.string.neue_patienten_hinweis)
            )

            else -> LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(list, key = { it.id }) { patient ->
                    PatientRow(patient = patient, onClick = { onPatientClick(patient.id) })
                }
            }
        }
    }
}

@Preview
@Composable
fun PatientsScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        PatientsScreen(onPatientClick = {}, viewModel = PatientsViewModel(PatientRepositoryMock()))
    }
}
