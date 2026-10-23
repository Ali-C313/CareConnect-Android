package com.example.careconnect.ui.patient.orders

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.domain.model.Order
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.theme.CareConnectTheme

// Mehrere Aufträge in einer Karte, getrennt durch Linien
@Composable
fun OrderGroup(
    orders: List<Order>,
    onOrderClick: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        orders.forEachIndexed { index, order ->
            OrderRow(order = order, onClick = { onOrderClick(order.id) })
            if (index < orders.lastIndex) {
                HorizontalDivider(Modifier.padding(start = 68.dp))
            }
        }
    }
}

@Preview
@Composable
fun OrderGroupPreview() {
    CareConnectTheme(darkTheme = false) {
        OrderGroup(orders = MockData.orders, onOrderClick = {})
    }
}
