package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.UserProfile
import com.google.firebase.Timestamp

// users/{uid}
data class UserProfileDto(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val birthDate: Timestamp? = null,
    val address: String? = null,
    val city: String? = null,
    val phone: String? = null,
    val profileImageURL: String? = null,
    val activeOrgId: String? = null,
    val createdAt: Timestamp? = null
)
fun UserProfileDto.toUserProfile(uid: String): UserProfile = UserProfile(
    uid = uid,
    firstName = firstName ?: "",
    lastName = lastName ?: "",
    email = email ?: "",
    birthDate = birthDate?.toMillis(),
    address = address ?: "",
    city = city ?: "",
    phone = phone ?: "",
    profileImageUrl = profileImageURL ?: "",
    activeOrgId = activeOrgId
)
