package com.example.careconnect.ui.employee.orderDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusOrange
import com.example.careconnect.ui.theme.icon

/** Kleine Karte mit Icon, Titel und Text */
@Composable
fun SmallSection(
    icon: ImageVector,
    tint: Color,
    title: String,
    text: String
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(icon, contentDescription = null, tint = tint)
        Column {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview
@Composable
fun SmallSectionPreview() {
    CareConnectTheme(darkTheme = false) {
        SmallSection(
            icon = Icons.Default.Info,
            tint = StatusOrange,
            title = "Allergien",
            text = MockData.patient.allergies
        )
    }
}
