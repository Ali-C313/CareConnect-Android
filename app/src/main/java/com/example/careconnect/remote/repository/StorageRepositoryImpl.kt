package com.example.careconnect.remote.repository

import com.example.careconnect.domain.repository.StorageRepository
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.tasks.await

class StorageRepositoryImpl(
    private val storage: FirebaseStorage
) : StorageRepository {

    /** @return Download-URL der hochgeladenen Datei */
    override suspend fun uploadFile(path: String, bytes: ByteArray, contentType: String): Result<String> = runCatching {
        val ref = storage.reference.child(path)
        val metadata = StorageMetadata.Builder()
            .setContentType(contentType)
            .build()
        ref.putBytes(bytes, metadata).await()
        ref.downloadUrl.await().toString()
    }

    override suspend fun deleteFile(path: String): Result<Unit> = runCatching {
        storage.reference.child(path).delete().await()
    }
}
