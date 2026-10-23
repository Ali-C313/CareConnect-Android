package com.example.careconnect.ui.patient.orders

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.R
import com.example.careconnect.domain.model.Order
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.DateHelper.toDateTimeString

@Composable
fun OrderHeaderCard(order: Order) {
    SectionCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                order.description.ifBlank { stringResource(R.string.auftrag) + " #${order.id.take(6)}" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            StatusChip(order.state)
        }
        (order.dueDate ?: order.createdAt)?.let {
            InfoRow(Icons.Default.Event, stringResource(R.string.datum), it.toDateTimeString())
        }
    }
}

@Preview
@Composable
fun OrderHeaderCardPreview() {
    CareConnectTheme(darkTheme = false) {
        OrderHeaderCard(order = MockData.orders.first())
    }
}
