package com.example.careconnect.domain.model

data class Invite(
    val code: String,
    val orgId: String,
    val orgName: String,
    val orgType: OrganizationType,
    val role: MemberRole
)
