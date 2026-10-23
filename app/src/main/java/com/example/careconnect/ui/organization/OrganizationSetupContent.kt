package com.example.careconnect.ui.organization

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.JoinRequestStatus
import com.example.careconnect.domain.model.OrganizationType
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.OrganizationRepositoryMock
import com.example.careconnect.mock.repository.UserRepositoryMock
import com.example.careconnect.ui.components.CareTextField
import com.example.careconnect.ui.components.FilterTabs
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusRed
import org.koin.androidx.compose.koinViewModel

/**
 * Beitreten per Code oder eigene Organisation gründen.
 * Wird im Onboarding und in den Einstellungen benutzt.
 */
@Composable
fun OrganizationSetupContent(
    viewModel: OrganizationViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val myRequests by viewModel.myRequests.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionCard(title = stringResource(R.string.code_beitreten), icon = Icons.Default.Key) {
            Text(
                text = stringResource(R.string.code_hinweis),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            CareTextField(
                label = stringResource(R.string.einladungscode),
                value = uiState.code,
                onValueChange = viewModel::onCodeChange,
                icon = Icons.Default.Key
            )
            Button(
                onClick = viewModel::join,
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp))
                } else {
                    Text(stringResource(R.string.beitreten), fontWeight = FontWeight.SemiBold)
                }
            }
        }

        SectionCard(title = stringResource(R.string.organisation_suchen), icon = Icons.Default.Search) {
            Text(
                text = stringResource(R.string.kein_code_hinweis),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            CareTextField(
                label = stringResource(R.string.name_ort),
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchChange,
                icon = Icons.Default.Search
            )
            if (uiState.isSearching) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp))
            } else if (uiState.searchQuery.trim().length >= 2 && uiState.searchResults.isEmpty()) {
                Text(stringResource(R.string.nichts_gefunden), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            uiState.searchResults.forEach { organization ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(organization.name, fontWeight = FontWeight.SemiBold)
                        Text(
                            listOf(organization.type.displayName, organization.address)
                                .filter { it.isNotBlank() }
                                .joinToString(" · "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(
                        onClick = { viewModel.sendRequest(organization) },
                        enabled = !uiState.isLoading
                    ) { Text(stringResource(R.string.anfragen)) }
                }
            }

            if (myRequests.isNotEmpty()) {
                HorizontalDivider(Modifier.padding(vertical = 4.dp))
                Text(stringResource(R.string.deine_anfragen), style = MaterialTheme.typography.labelMedium)
                myRequests.forEach { request ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(request.orgName, fontWeight = FontWeight.SemiBold)
                            Text(
                                request.status.displayName,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (request.status == JoinRequestStatus.DECLINED) StatusRed
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(onClick = { viewModel.cancelRequest(request.orgId) }) {
                            Text(if (request.status == JoinRequestStatus.DECLINED) stringResource(R.string.entfernen) else stringResource(R.string.zurueckziehen))
                        }
                    }
                }
            }
        }

        SectionCard(title = stringResource(R.string.organisation_gruenden), icon = Icons.Default.Business) {
            Text(
                text = stringResource(R.string.organisation_gruenden_hinweis),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            CareTextField(
                label = stringResource(R.string.name),
                value = uiState.orgName,
                onValueChange = viewModel::onOrgNameChange,
                icon = Icons.Default.Business
            )
            FilterTabs(
                options = OrganizationType.entries,
                selected = uiState.orgType,
                label = { it.displayName },
                onSelect = viewModel::onOrgTypeChange
            )
            CareTextField(
                label = stringResource(R.string.adresse_optional),
                value = uiState.orgAddress,
                onValueChange = viewModel::onOrgAddressChange,
                icon = Icons.Default.Place
            )
            OutlinedButton(
                onClick = viewModel::create,
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(stringResource(R.string.organisation_gruenden), fontWeight = FontWeight.SemiBold)
            }
        }

        uiState.errorMessage?.let { error ->
            Text(
                text = error,
                color = StatusRed,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun OrganizationSetupContentPreview() {
    CareConnectTheme(darkTheme = false) {
        OrganizationSetupContent(
            OrganizationViewModel(
                AuthRepositoryMock(),
                UserRepositoryMock(),
                OrganizationRepositoryMock()
            )
        )
    }
}
