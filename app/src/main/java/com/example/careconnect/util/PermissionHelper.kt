package com.example.careconnect.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

// Ablauf für Laufzeit-Berechtigungen: vorhanden, Erklärung zeigen oder anfragen
object PermissionHelper {

    private fun hasAnyPermission(context: Context, permissions: Array<String>): Boolean =
        permissions.any {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    fun handleActionWithPermission(
        context: Context,
        launcher: ActivityResultLauncher<Array<String>>,
        permissions: Array<String>,
        action: () -> Unit,
        showRationale: () -> Unit
    ) {
        when {
            // 1. Fall - Berechtigung besteht
            hasAnyPermission(context, permissions) -> action()

            // 2. Fall - Schon einmal abgelehnt, Erklärung anzeigen
            (context as? Activity)?.let { activity ->
                permissions.any { ActivityCompat.shouldShowRequestPermissionRationale(activity, it) }
            } == true -> showRationale()

            // 3. Fall - Nach Berechtigung fragen
            else -> launcher.launch(permissions)
        }
    }
}
