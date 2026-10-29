package com.example.careconnect.ui.patient.orders

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.ui.theme.CareConnectTheme

/** Überschrift mit Anzahl über einer Auftragsgruppe */
@Composable
fun SectionTitle(title: String, count: Int) {
    Text(
        text = if (count > 0) "$title ($count)" else title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Preview
@Composable
fun SectionTitlePreview() {
    CareConnectTheme(darkTheme = false) {
        SectionTitle(title = "Offen", count = 2)
    }
}
