package com.example.careconnect.ui.employee.orderDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.Order
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.color
import com.example.careconnect.ui.theme.icon
import com.example.careconnect.util.DateHelper.toDateTimeString

/** Aktueller Status des Auftrags */
@Composable
fun StatusCard(
    order: Order,
    employeeName: String?
) {
    SectionCard(title = stringResource(R.string.status), icon = Icons.Default.Schedule) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(order.state.icon, contentDescription = null, tint = order.state.color)
            Text(
                text = order.state.displayName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = order.state.color
            )
        }
        order.createdAt?.let {
            InfoRow(Icons.Default.Schedule, stringResource(R.string.erstellt_am), it.toDateTimeString())
        }
        order.dueDate?.let {
            InfoRow(Icons.Default.CalendarMonth, stringResource(R.string.faellig_am), it.toDateTimeString())
        }
        if (employeeName != null) {
            InfoRow(Icons.Default.Person, stringResource(R.string.mitarbeiter), employeeName)
        }
    }
}

@Preview
@Composable
fun StatusCardPreview() {
    CareConnectTheme(darkTheme = false) {
        StatusCard(order = MockData.orders.first(), employeeName = MockData.employee.name)
    }
}
