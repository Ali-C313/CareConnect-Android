package com.example.careconnect.ui.patient.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.color

/** Farbiger Chip für den Auftragsstatus */
@Composable
fun StatusChip(state: OrderState) {
    val color: Color = state.color
    Text(
        text = state.patientLabel,
        color = color,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Preview
@Composable
fun StatusChipPreview() {
    CareConnectTheme(darkTheme = false) {
        StatusChip(state = OrderState.AKTUELL)
    }
}
