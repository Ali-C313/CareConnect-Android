package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.Invite
import com.example.careconnect.domain.model.MemberRole
import com.example.careconnect.domain.model.OrganizationType
import com.google.firebase.Timestamp

data class InviteDto(
    val orgId: String? = null,
    val orgName: String? = null,
    val orgType: String? = null,
    val role: String? = null,
    val createdBy: String? = null,
    val createdAt: Timestamp? = null
)
fun InviteDto.toInvite(code: String): Invite? {
    val id = orgId ?: return null
    return Invite(
        code = code,
        orgId = id,
        orgName = orgName ?: "",
        orgType = OrganizationType.fromValue(orgType),
        role = MemberRole.fromValue(role)
    )
}
