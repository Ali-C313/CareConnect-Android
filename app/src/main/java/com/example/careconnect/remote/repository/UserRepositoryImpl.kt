package com.example.careconnect.remote.repository

import com.example.careconnect.domain.model.UserProfile
import com.example.careconnect.domain.repository.UserRepository
import com.example.careconnect.remote.CollectionNames
import com.example.careconnect.remote.listenAsFlow
import com.example.careconnect.remote.model.UserProfileDto
import com.example.careconnect.remote.model.toTimestamp
import com.example.careconnect.remote.model.toUserProfile
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

// Profil des Nutzers unter users/{uid}
class UserRepositoryImpl(
    private val db: FirebaseFirestore
) : UserRepository {

    private fun userRef(uid: String) = db.collection(CollectionNames.USERS).document(uid)

    override fun observeProfile(uid: String): Flow<UserProfile?> = userRef(uid).listenAsFlow()
        .map { doc ->
            if (!doc.exists()) null
            else runCatching { doc.toObject<UserProfileDto>()?.toUserProfile(doc.id) }.getOrNull()
        }
        .catch { emit(null) }

    override suspend fun getProfile(uid: String): Result<UserProfile?> = runCatching {
        val doc = userRef(uid).get().await()
        if (!doc.exists()) null else doc.toObject<UserProfileDto>()?.toUserProfile(doc.id)
    }

    override suspend fun createProfile(profile: UserProfile): Result<Unit> = runCatching {
        val dto = UserProfileDto(
            firstName = profile.firstName,
            lastName = profile.lastName,
            email = profile.email,
            birthDate = profile.birthDate?.toTimestamp(),
            address = profile.address,
            city = profile.city,
            phone = profile.phone,
            profileImageURL = profile.profileImageUrl,
            activeOrgId = null,
            createdAt = Timestamp.now()
        )
        userRef(profile.uid).set(dto, SetOptions.merge()).await()
        Unit
    }

    override suspend fun setActiveOrg(uid: String, orgId: String): Result<Unit> = runCatching {
        userRef(uid).update("activeOrgId", orgId).await()
        Unit
    }
}
