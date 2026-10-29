package com.example.careconnect.mock.repository

import com.example.careconnect.domain.model.UserProfile
import com.example.careconnect.domain.repository.UserRepository
import com.example.careconnect.mock.MockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

// Beispielprofil für die Previews
class UserRepositoryMock : UserRepository {

    private val profiles = MutableStateFlow(listOf(MockData.profile))

    override fun observeProfile(uid: String): Flow<UserProfile?> =
        profiles.map { list -> list.firstOrNull { it.uid == uid } }

    override suspend fun getProfile(uid: String): Result<UserProfile?> = runCatching {
        profiles.value.firstOrNull { it.uid == uid }
    }

    override suspend fun createProfile(profile: UserProfile): Result<Unit> = runCatching {
        profiles.update { it + profile }
    }

    override suspend fun setActiveOrg(uid: String, orgId: String): Result<Unit> = runCatching {
        profiles.update { list -> list.map { if (it.uid == uid) it.copy(activeOrgId = orgId) else it } }
    }
}
