package com.example.careconnect.mock.repository

import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.mock.MockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

// uid bestimmt, als wer man in der Preview eingeloggt ist
class AuthRepositoryMock(
    uid: String? = MockData.employee.id
) : AuthRepository {

    private val _authState = MutableStateFlow(uid)
    override val authState: Flow<String?> = _authState

    override val currentUserId: String?
        get() = _authState.value

    override val currentUserEmail: String?
        get() = _authState.value?.let { "$it@careconnect.de" }

    override suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        _authState.update { MockData.employee.id }
    }

    override suspend fun register(email: String, password: String): Result<String> = runCatching {
        "neu"
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        _authState.update { null }
    }

    override suspend fun changePassword(currentPassword: String?, newPassword: String): Result<Unit> = runCatching { }
}
