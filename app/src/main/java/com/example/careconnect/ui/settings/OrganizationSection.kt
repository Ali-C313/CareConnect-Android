package com.example.careconnect.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.MemberRole
import com.example.careconnect.domain.model.Membership
import com.example.careconnect.mock.device.LocationTrackerMock
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.EmployeeRepositoryMock
import com.example.careconnect.mock.repository.LocationRepositoryMock
import com.example.careconnect.mock.repository.OrganizationRepositoryMock
import com.example.careconnect.mock.repository.PatientRepositoryMock
import com.example.careconnect.mock.repository.SettingsRepositoryMock
import com.example.careconnect.mock.repository.UserRepositoryMock
import com.example.careconnect.service.TrackingManager
import com.example.careconnect.session.OrgSession
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.organization.OrganizationSetupContent
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusRed

/**
 * Aktuelle Organisation, Wechsel zwischen Organisationen,
 * Verwalten (nur Leitung) und Austreten.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationSection(
    viewModel: SettingsViewModel,
    onManageClick: (() -> Unit)? = null
) {
    val memberships by viewModel.memberships.collectAsState()
    val current by viewModel.currentMembership.collectAsState()
    val message by viewModel.message.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }
    var leaveTarget by remember { mutableStateOf<Membership?>(null) }

    SectionCard(title = stringResource(R.string.organisation), icon = Icons.Default.Business) {
        current?.let { membership ->
            Text(membership.orgName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                "${membership.orgType.displayName} · ${membership.role.displayName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        val others = memberships.filter { it.orgId != current?.orgId }
        if (others.isNotEmpty()) {
            HorizontalDivider(Modifier.padding(vertical = 4.dp))
            Text(stringResource(R.string.wechseln), style = MaterialTheme.typography.labelMedium)
            others.forEach { membership ->
                TextButton(
                    onClick = { viewModel.switchOrganization(membership.orgId) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.AutoMirrored.Filled.CompareArrows, contentDescription = null)
                    Text("  ${membership.orgName} (${membership.role.displayName})", modifier = Modifier.weight(1f))
                }
            }
        }

        if (current?.role == MemberRole.ADMIN && onManageClick != null) {
            OutlinedButton(onClick = onManageClick, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.ManageAccounts, contentDescription = null)
                Text(stringResource(R.string.organisation_verwalten))
            }
        }

        TextButton(onClick = { showAddSheet = true }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Add, contentDescription = null)
            Text(stringResource(R.string.weitere_organisation_hinzufuegen))
        }

        if (current != null && current?.role != MemberRole.ADMIN) {
            TextButton(onClick = { leaveTarget = current }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = StatusRed)
                Text(stringResource(R.string.organisation_verlassen), color = StatusRed)
            }
        }
    }

    leaveTarget?.let { membership ->
        AlertDialog(
            onDismissRequest = { leaveTarget = null },
            title = { Text(stringResource(R.string.organisation_verlassen_frage, membership.orgName)) },
            text = { Text(stringResource(R.string.hast_danach_keinen_zugriff_mehr)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.leaveOrganization()
                    leaveTarget = null
                }) { Text(stringResource(R.string.verlassen), color = StatusRed) }
            },
            dismissButton = {
                TextButton(onClick = { leaveTarget = null }) { Text(stringResource(R.string.abbrechen)) }
            }
        )
    }

    if (showAddSheet) {
        ModalBottomSheet(onDismissRequest = { showAddSheet = false }) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                OrganizationSetupContent()
            }
        }
    }

    message?.let { text ->
        AlertDialog(
            onDismissRequest = viewModel::dismissMessage,
            title = { Text(stringResource(R.string.organisation)) },
            text = { Text(text) },
            confirmButton = { TextButton(onClick = viewModel::dismissMessage) { Text(stringResource(R.string.ok)) } }
        )
    }
}

@Preview
@Composable
fun OrganizationSectionPreview() {
    CareConnectTheme(darkTheme = false) {
        OrganizationSection(
            viewModel = SettingsViewModel(
                SettingsRepositoryMock(),
                AuthRepositoryMock(),
                TrackingManager(LocationTrackerMock(), LocationRepositoryMock()),
                UserRepositoryMock(),
                OrganizationRepositoryMock(),
                OrgSession(),
                EmployeeRepositoryMock(),
                PatientRepositoryMock()
            ),
            onManageClick = {}
        )
    }
}
