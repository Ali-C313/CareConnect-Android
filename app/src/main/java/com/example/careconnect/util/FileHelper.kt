package com.example.careconnect.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Dateien lesen, die der Nutzer im Datei-Dialog ausgewählt hat
object FileHelper {

    /** @return Inhalt der gewählten Datei oder null, wenn sie nicht gelesen werden kann */
    suspend fun Context.readBytes(uri: Uri): ByteArray? = withContext(Dispatchers.IO) {
        runCatching {
            contentResolver.openInputStream(uri)?.use { it.readBytes() }
        }.getOrNull()
    }

    /** @return Dateiname, z.B. "Vertrag.pdf" */
    fun Context.fileName(uri: Uri): String {
        val name = runCatching {
            contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else null
            }
        }.getOrNull()
        return name ?: uri.lastPathSegment ?: "Dokument.pdf"
    }
}
