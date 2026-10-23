package com.example.careconnect.ui.employee.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.Order
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.color
import com.example.careconnect.util.DateHelper.toDateTimeString

/**
 * Karte in der Auftragsliste.
 * @param employeeName Name des zuständigen Mitarbeiters oder "Noch offen"
 */
@Composable
fun OrderCard(
    order: Order,
    employeeName: String,
    modifier: Modifier = Modifier
) {
    SectionCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📋 ${order.patientName.ifEmpty { "Unbekannt" }}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = order.state.displayName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = order.state.color,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(order.state.color.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        if (order.description.isNotEmpty()) {
            Text(
                text = order.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        } else {
            Text(
                text = stringResource(R.string.keine_beschreibung_vorhanden),
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        order.dueDate?.let { dueDate ->
            SmallInfo(Icons.Default.CalendarMonth, stringResource(R.string.faellig_datum, dueDate.toDateTimeString()))
        }
        SmallInfo(Icons.Default.Person, employeeName)
    }
}

@Preview
@Composable
fun OrderCardPreview() {
    CareConnectTheme(darkTheme = false) {
        OrderCard(order = MockData.orders[1], employeeName = "Noch offen")
    }
}
