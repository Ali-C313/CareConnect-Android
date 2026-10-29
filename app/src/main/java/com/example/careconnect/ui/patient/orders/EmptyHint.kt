package com.example.careconnect.ui.patient.orders

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.color

/** Kurzer Hinweis, wenn eine Gruppe leer ist */
@Composable
fun EmptyHint(text: String) {
    Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
}

@Preview
@Composable
fun EmptyHintPreview() {
    CareConnectTheme(darkTheme = false) {
        EmptyHint(text = "Keine offenen Aufträge.")
    }
}
