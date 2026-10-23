package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.JoinRequest
import com.example.careconnect.domain.model.JoinRequestStatus
import com.example.careconnect.domain.model.OrganizationType
import com.google.firebase.Timestamp

data class JoinRequestDto(
    val orgId: String? = null,
    val orgName: String? = null,
    val orgType: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val address: String? = null,
    val city: String? = null,
    val phone: String? = null,
    val birthDate: Timestamp? = null,
    val profileImageURL: String? = null,
    val status: String? = null,
    val createdAt: Timestamp? = null
)
fun JoinRequestDto.toJoinRequest(uid: String): JoinRequest = JoinRequest(
    orgId = orgId ?: "",
    orgName = orgName ?: "",
    orgType = OrganizationType.fromValue(orgType),
    uid = uid,
    firstName = firstName ?: "",
    lastName = lastName ?: "",
    email = email ?: "",
    address = address ?: "",
    city = city ?: "",
    phone = phone ?: "",
    birthDate = birthDate?.toMillis(),
    profileImageUrl = profileImageURL ?: "",
    status = JoinRequestStatus.fromValue(status),
    createdAt = createdAt?.toMillis()
)
