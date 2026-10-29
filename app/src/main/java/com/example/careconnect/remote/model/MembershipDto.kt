package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.MemberRole
import com.example.careconnect.domain.model.Membership
import com.example.careconnect.domain.model.OrganizationType
import com.google.firebase.Timestamp

// users/{uid}/memberships
data class MembershipDto(
    val orgName: String? = null,
    val orgType: String? = null,
    val role: String? = null,
    val joinedAt: Timestamp? = null
)

fun MembershipDto.toMembership(orgId: String): Membership = Membership(
    orgId = orgId,
    orgName = orgName ?: "",
    orgType = OrganizationType.fromValue(orgType),
    role = MemberRole.fromValue(role)
)
