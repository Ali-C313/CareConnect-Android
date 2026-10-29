package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.Member
import com.example.careconnect.domain.model.MemberRole
import com.google.firebase.Timestamp

// organizations/{id}/members
data class MemberDto(
    val role: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val inviteCode: String? = null,
    val joinedAt: Timestamp? = null
)

fun MemberDto.toMember(uid: String): Member = Member(
    uid = uid,
    firstName = firstName ?: "",
    lastName = lastName ?: "",
    role = MemberRole.fromValue(role)
)
