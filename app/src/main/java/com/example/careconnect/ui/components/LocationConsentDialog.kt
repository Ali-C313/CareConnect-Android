package com.example.careconnect.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.R
import com.example.careconnect.ui.theme.CareConnectTheme

// Mitarbeiter bestätigt, dass sein Standort während des Einsatzes geteilt wird
@Composable
fun LocationConsentDialog(
    onAllow: () -> Unit,
    onDeny: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        icon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
        title = { Text(stringResource(R.string.standort_teilen)) },
        text = { Text(stringResource(R.string.standort_teilen_hinweis)) },
        confirmButton = {
            TextButton(onClick = onAllow) { Text(stringResource(R.string.teilen)) }
        },
        dismissButton = {
            TextButton(onClick = onDeny) { Text(stringResource(R.string.nicht_teilen)) }
        }
    )
}

@Preview
@Composable
fun LocationConsentDialogPreview() {
    CareConnectTheme(darkTheme = false) {
        LocationConsentDialog(onAllow = {}, onDeny = {})
    }
}
