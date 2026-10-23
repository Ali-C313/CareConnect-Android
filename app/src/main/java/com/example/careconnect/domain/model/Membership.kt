package com.example.careconnect.domain.model

// Eintrag unter users/{uid}/memberships
data class Membership(
    val orgId: String,
    val orgName: String,
    val orgType: OrganizationType,
    val role: MemberRole
)
