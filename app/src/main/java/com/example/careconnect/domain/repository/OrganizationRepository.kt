package com.example.careconnect.domain.repository

import com.example.careconnect.domain.model.Invite
import com.example.careconnect.domain.model.JoinRequest
import com.example.careconnect.domain.model.Member
import com.example.careconnect.domain.model.MemberRole
import com.example.careconnect.domain.model.Membership
import com.example.careconnect.domain.model.Organization
import com.example.careconnect.domain.model.OrganizationType
import com.example.careconnect.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface OrganizationRepository {
    fun observeMemberships(uid: String): Flow<List<Membership>>
    fun observeOrganization(orgId: String): Flow<Organization?>

    // null = kein Mitglied (mehr)
    fun observeMemberRole(orgId: String, uid: String): Flow<MemberRole?>

    /** @return ID der neuen Organisation */
    suspend fun createOrganization(
        profile: UserProfile,
        name: String,
        type: OrganizationType,
        address: String
    ): Result<String>

    suspend fun leaveOrganization(uid: String, orgId: String): Result<Unit>

    // Einladungscodes
    fun observeInvites(orgId: String): Flow<List<Invite>>
    suspend fun findInvite(code: String): Result<Invite?>
    suspend fun joinWithInvite(profile: UserProfile, invite: Invite): Result<Unit>
    suspend fun createInvite(orgId: String, role: MemberRole): Result<String>
    suspend fun deleteInvite(orgId: String, code: String): Result<Unit>

    // Suche und Beitrittsanfragen
    suspend fun searchOrganizations(query: String): Result<List<Organization>>
    fun observeIsListed(orgId: String): Flow<Boolean>
    suspend fun setListed(orgId: String, listed: Boolean): Result<Unit>
    suspend fun sendJoinRequest(profile: UserProfile, organization: Organization): Result<Unit>
    fun observeMyJoinRequests(uid: String): Flow<List<JoinRequest>>
    suspend fun cancelJoinRequest(uid: String, orgId: String): Result<Unit>
    suspend fun completeAcceptedRequest(uid: String, request: JoinRequest): Result<Unit>
    fun observeJoinRequests(orgId: String): Flow<List<JoinRequest>>
    suspend fun acceptJoinRequest(request: JoinRequest): Result<Unit>
    suspend fun declineJoinRequest(request: JoinRequest): Result<Unit>

    // Mitglieder verwalten
    fun observeMembers(orgId: String): Flow<List<Member>>
    suspend fun updateMemberRole(orgId: String, uid: String, role: MemberRole): Result<Unit>
    suspend fun removeMember(orgId: String, uid: String): Result<Unit>
}
