package com.example.careconnect.mock.repository

import com.example.careconnect.domain.repository.StorageRepository

// Upload ohne Firebase für die Previews
class StorageRepositoryMock : StorageRepository {

    override suspend fun uploadFile(path: String, bytes: ByteArray, contentType: String): Result<String> = runCatching {
        "https://example.com/$path"
    }

    override suspend fun deleteFile(path: String): Result<Unit> = runCatching { }
}
