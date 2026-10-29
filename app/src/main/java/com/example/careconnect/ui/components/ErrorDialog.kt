package com.example.careconnect.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.R
import com.example.careconnect.ui.theme.CareConnectTheme

/** Dialog für Fehler aus dem ViewModel, wird nur bei message != null gezeigt */
@Composable
fun ErrorDialog(
    message: String?,
    onDismiss: () -> Unit
) {
    if (message == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.fehler)) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.ok)) }
        }
    )
}

@Preview
@Composable
fun ErrorDialogPreview() {
    CareConnectTheme(darkTheme = false) {
        ErrorDialog(message = "Fehler beim Speichern: Keine Verbindung", onDismiss = {})
    }
}
