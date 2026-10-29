package com.example.careconnect.ui.employee.organization

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.JoinRequest
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.theme.CareConnectTheme

/** Eine Beitrittsanfrage mit Annehmen und Ablehnen */
@Composable
fun RequestRow(
    request: JoinRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Text(request.name, fontWeight = FontWeight.SemiBold)
        val details = listOf(listOf(request.address, request.city).filter { it.isNotBlank() }.joinToString(", "), request.email)
            .filter { it.isNotBlank() }
        details.forEach { line ->
            Text(line, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onAccept, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.annehmen)) }
            OutlinedButton(onClick = onDecline, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.ablehnen)) }
        }
    }
}

@Preview
@Composable
fun RequestRowPreview() {
    CareConnectTheme(darkTheme = false) {
        RequestRow(request = MockData.joinRequests.first(), onAccept = {}, onDecline = {})
    }
}
