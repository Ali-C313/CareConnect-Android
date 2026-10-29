package com.example.careconnect.ui.employee.prescriptions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.theme.CareConnectTheme

/** Zeile mit Icon und Text in der Verordnung */
@Composable
fun InfoLine(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview
@Composable
fun InfoLinePreview() {
    CareConnectTheme(darkTheme = false) {
        InfoLine(icon = Icons.Default.Person, text = MockData.patient.name)
    }
}
