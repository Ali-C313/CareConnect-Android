package com.example.careconnect.domain.repository

interface StorageRepository {
    // Lädt die Datei hoch und gibt die Download-URL zurück
    suspend fun uploadFile(path: String, bytes: ByteArray, contentType: String): Result<String>
    suspend fun deleteFile(path: String): Result<Unit>
}
