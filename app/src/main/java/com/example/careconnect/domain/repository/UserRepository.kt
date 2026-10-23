package com.example.careconnect.domain.repository

import com.example.careconnect.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    // null, solange es kein Profil unter users/{uid} gibt
    fun observeProfile(uid: String): Flow<UserProfile?>
    suspend fun getProfile(uid: String): Result<UserProfile?>
    suspend fun createProfile(profile: UserProfile): Result<Unit>
    suspend fun setActiveOrg(uid: String, orgId: String): Result<Unit>
}
