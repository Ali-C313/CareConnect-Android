package com.example.careconnect.ui.employee.organization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.Member
import com.example.careconnect.domain.model.MemberRole
import com.example.careconnect.mock.MockData
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.OrganizationRepositoryMock
import com.example.careconnect.session.OrgSession
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusRed
import org.koin.androidx.compose.koinViewModel

/** Leitung verwaltet Anfragen, Codes und Mitglieder */
@Composable
fun ManageOrganizationScreen(
    onBack: () -> Unit,
    viewModel: ManageOrganizationViewModel = koinViewModel()
) {
    val organization by viewModel.organization.collectAsState()
    val isListed by viewModel.isListed.collectAsState()
    val requests by viewModel.joinRequests.collectAsState()
    val invites by viewModel.invites.collectAsState()
    val members by viewModel.members.collectAsState()
    val message by viewModel.message.collectAsState()
    var memberToRemove by remember { mutableStateOf<Member?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareTopBar(title = organization?.name ?: stringResource(R.string.organisation), onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard(title = stringResource(R.string.sichtbarkeit), icon = Icons.Default.Visibility) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(stringResource(R.string.suche_anzeigen))
                        Text(
                            stringResource(R.string.suche_hinweis),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = isListed, onCheckedChange = viewModel::setListed)
                }
            }

            SectionCard(title = stringResource(R.string.beitrittsanfragen, requests.size), icon = Icons.Default.PersonAdd) {
                if (requests.isEmpty()) {
                    Text(stringResource(R.string.keine_offenen_anfragen), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                requests.forEach { request ->
                    RequestRow(
                        request = request,
                        onAccept = { viewModel.accept(request) },
                        onDecline = { viewModel.decline(request) }
                    )
                }
            }

            SectionCard(title = stringResource(R.string.einladungscodes), icon = Icons.Default.Key) {
                Text(
                    stringResource(R.string.codes_weitergeben_hinweis),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                invites.sortedBy { it.role.ordinal }.forEach { invite ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            SelectionContainer {
                                Text(
                                    invite.code,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                stringResource(R.string.fuer_rolle, invite.role.displayName),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { viewModel.deleteInvite(invite.code) }) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.code_loeschen), tint = StatusRed)
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.createInvite(MemberRole.STAFF) },
                        modifier = Modifier.weight(1f)
                    ) { Text(stringResource(R.string.mitarbeiter_plus)) }
                    OutlinedButton(
                        onClick = { viewModel.createInvite(MemberRole.PATIENT) },
                        modifier = Modifier.weight(1f)
                    ) { Text(stringResource(R.string.patient_plus)) }
                }
            }

            SectionCard(title = stringResource(R.string.mitglieder, members.size), icon = Icons.Default.Groups) {
                members.forEach { member ->
                    MemberRow(
                        member = member,
                        isSelf = member.uid == viewModel.currentUserId,
                        onRoleChange = { role -> viewModel.changeRole(member, role) },
                        onRemove = { memberToRemove = member }
                    )
                }
            }
        }
    }

    memberToRemove?.let { member ->
        AlertDialog(
            onDismissRequest = { memberToRemove = null },
            title = { Text(stringResource(R.string.person_entfernen_frage, member.name)) },
            text = { Text(stringResource(R.string.mitglied_entfernen_hinweis)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.remove(member)
                    memberToRemove = null
                }) { Text(stringResource(R.string.entfernen), color = StatusRed) }
            },
            dismissButton = {
                TextButton(onClick = { memberToRemove = null }) { Text(stringResource(R.string.abbrechen)) }
            }
        )
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
fun ManageOrganizationScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        ManageOrganizationScreen(
            onBack = {},
            viewModel = ManageOrganizationViewModel(
                OrganizationRepositoryMock(),
                AuthRepositoryMock(),
                OrgSession().apply { set(MockData.organization.id) }
            )
        )
    }
}
