package com.example.careconnect.ui.employee.organization

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.R
import com.example.careconnect.domain.model.Member
import com.example.careconnect.domain.model.MemberRole
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusRed

@Composable
fun MemberRow(
    member: Member,
    isSelf: Boolean,
    onRoleChange: (MemberRole) -> Unit,
    onRemove: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(if (isSelf) stringResource(R.string.name_du, member.name) else member.name, fontWeight = FontWeight.SemiBold)
            Text(
                member.role.displayName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Sich selbst kann man nicht ändern, damit immer eine Leitung bleibt
        if (!isSelf) {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.optionen))
            }
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                when (member.role) {
                    MemberRole.STAFF -> DropdownMenuItem(
                        text = { Text(stringResource(R.string.leitung_machen)) },
                        onClick = {
                            menuExpanded = false
                            onRoleChange(MemberRole.ADMIN)
                        }
                    )
                    MemberRole.ADMIN -> DropdownMenuItem(
                        text = { Text(stringResource(R.string.mitarbeiter_machen)) },
                        onClick = {
                            menuExpanded = false
                            onRoleChange(MemberRole.STAFF)
                        }
                    )
                    MemberRole.PATIENT -> Unit
                }
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.entfernen), color = StatusRed) },
                    onClick = {
                        menuExpanded = false
                        onRemove()
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun MemberRowPreview() {
    CareConnectTheme(darkTheme = false) {
        MemberRow(member = MockData.members[1], isSelf = false, onRoleChange = {}, onRemove = {})
    }
}
