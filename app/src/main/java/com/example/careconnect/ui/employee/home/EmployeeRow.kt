package com.example.careconnect.ui.employee.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.domain.model.Employee
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.AvatarView
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.color
import com.example.careconnect.util.ResourceHelper
import com.example.careconnect.util.StringHelper

@Composable
fun EmployeeRow(
    employee: Employee,
    onClick: () -> Unit
) {
    SectionCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AvatarView(
                initials = StringHelper.initials(employee.name),
                imageUrl = employee.profileImageUrl,
                borderColor = employee.status.color
            )
            Column {
                Text(
                    text = employee.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = ResourceHelper.translate(employee.role),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = employee.status.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = employee.status.color
                )
            }
        }
    }
}

@Preview
@Composable
fun EmployeeRowPreview() {
    CareConnectTheme(darkTheme = false) {
        EmployeeRow(employee = MockData.employee, onClick = {})
    }
}
