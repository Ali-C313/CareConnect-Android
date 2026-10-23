package com.example.careconnect.ui.employee.employeeProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.Employee
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.AvatarView
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.color
import com.example.careconnect.util.DateHelper.toDateString
import com.example.careconnect.util.ResourceHelper
import com.example.careconnect.util.StringHelper

@Composable
fun ProfileHeader(employee: Employee) {
    SectionCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AvatarView(
                initials = StringHelper.initials(employee.name),
                imageUrl = employee.profileImageUrl,
                size = 72.dp
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = employee.name.ifEmpty { "—" },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(ResourceHelper.translate(employee.role.ifEmpty { "—" }), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    employee.email.ifEmpty { "—" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(employee.status.color)
                    )
                    Text(
                        employee.status.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        color = employee.status.color
                    )
                }
                employee.birthDate?.let {
                    Text(
                        stringResource(R.string.geboren_datum, it.toDateString()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ProfileHeaderPreview() {
    CareConnectTheme(darkTheme = false) {
        ProfileHeader(employee = MockData.employee)
    }
}
