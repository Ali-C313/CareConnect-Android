package com.example.careconnect.ui.components

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.careconnect.R
import com.example.careconnect.util.PermissionHelper

private val locationPermissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

/**
 * Fragt Benachrichtigungen nach dem Login und den Standort, sobald ein Einsatz läuft.
 * @param onLocationGranted startet das Tracking neu
 */
@Composable
fun PermissionRequests(
    isTracking: Boolean,
    onLocationGranted: () -> Unit
) {
    val context = LocalContext.current
    var showLocationExplanation by rememberSaveable { mutableStateOf(false) }
    var showNotificationExplanation by rememberSaveable { mutableStateOf(false) }

    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.any { it }) onLocationGranted()
    }

    // Ohne Erlaubnis werden einfach keine Benachrichtigungen gezeigt
    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    // Benachrichtigungen gibt es erst ab Android 13 als Laufzeit-Berechtigung
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionHelper.handleActionWithPermission(
                context = context,
                launcher = notificationLauncher,
                permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                action = {},
                showRationale = { showNotificationExplanation = true }
            )
        }
    }

    // Erst fragen, wenn ein Mitarbeiter einen Auftrag angenommen hat
    LaunchedEffect(isTracking) {
        if (isTracking) {
            PermissionHelper.handleActionWithPermission(
                context = context,
                launcher = locationLauncher,
                permissions = locationPermissions,
                action = {},
                showRationale = { showLocationExplanation = true }
            )
        }
    }

    if (showNotificationExplanation && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        PermissionExplanationDialog(
            text = stringResource(R.string.benachrichtigung_berechtigung_erklaerung),
            onDismiss = { showNotificationExplanation = false },
            onConfirm = {
                showNotificationExplanation = false
                notificationLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
            }
        )
    }

    if (showLocationExplanation) {
        PermissionExplanationDialog(
            text = stringResource(R.string.standort_berechtigung_erklaerung),
            onDismiss = { showLocationExplanation = false },
            onConfirm = {
                showLocationExplanation = false
                locationLauncher.launch(locationPermissions)
            }
        )
    }
}
