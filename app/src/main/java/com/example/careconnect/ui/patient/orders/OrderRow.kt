package com.example.careconnect.ui.patient.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusGreen
import com.example.careconnect.ui.theme.StatusOrange
import com.example.careconnect.ui.theme.color
import com.example.careconnect.util.DateHelper.toDateTimeString

@Composable
fun OrderRow(order: Order, onClick: () -> Unit) {
    val (icon, tint) = when (order.state) {
        OrderState.OFFEN -> Icons.Default.Schedule to StatusOrange
        OrderState.AKTUELL -> Icons.Default.DirectionsCar to order.state.color
        OrderState.ERLEDIGT -> Icons.Default.CheckCircle to StatusGreen
    }
    val date = when (order.state) {
        OrderState.ERLEDIGT -> order.report?.completedAt ?: order.dueDate ?: order.createdAt
        else -> order.dueDate ?: order.createdAt
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(tint.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint)
        }
        Column(Modifier.weight(1f)) {
            Text(
                order.description.ifBlank { stringResource(R.string.auftrag) + " #${order.id.take(6)}" },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            date?.let {
                Text(
                    it.toDateTimeString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        StatusChip(order.state)
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
fun OrderRowPreview() {
    CareConnectTheme(darkTheme = false) {
        OrderRow(order = MockData.orders[1], onClick = {})
    }
}
