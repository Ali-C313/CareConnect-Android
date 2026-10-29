package com.example.careconnect.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.R
import com.example.careconnect.ui.theme.CareConnectTheme

/** Erklärt, wofür eine Berechtigung gebraucht wird, OK fragt erneut an */
@Composable
fun PermissionExplanationDialog(
    text: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.berechtigung_erforderlich)) },
        text = { Text(text) },
        confirmButton = {
            Button(onClick = onConfirm) { Text(stringResource(R.string.ok)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.abbrechen)) }
        }
    )
}

@Preview
@Composable
fun PermissionExplanationDialogPreview() {
    CareConnectTheme(darkTheme = false) {
        PermissionExplanationDialog(
            text = stringResource(R.string.standort_berechtigung_erklaerung),
            onDismiss = {},
            onConfirm = {}
        )
    }
}
