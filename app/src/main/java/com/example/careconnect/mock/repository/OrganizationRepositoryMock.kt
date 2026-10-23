package com.example.careconnect.mock.repository

import com.example.careconnect.domain.model.Invite
import com.example.careconnect.domain.model.JoinRequest
import com.example.careconnect.domain.model.JoinRequestStatus
import com.example.careconnect.domain.model.Member
import com.example.careconnect.domain.model.MemberRole
import com.example.careconnect.domain.model.Membership
import com.example.careconnect.domain.model.Organization
import com.example.careconnect.domain.model.OrganizationType
import com.example.careconnect.domain.model.UserProfile
import com.example.careconnect.domain.repository.OrganizationRepository
import com.example.careconnect.mock.MockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class OrganizationRepositoryMock : OrganizationRepository {

    private val organizations = MutableStateFlow(listOf(MockData.organization, MockData.practice))
    private val memberships = MutableStateFlow(listOf(MockData.membership))
    private val invites = MutableStateFlow(MockData.invites)
    private val requests = MutableStateFlow(MockData.joinRequests)
    private val members = MutableStateFlow(MockData.members)
    private val listed = MutableStateFlow(true)

    override fun observeMemberships(uid: String): Flow<List<Membership>> = memberships

    override fun observeOrganization(orgId: String): Flow<Organization?> =
        organizations.map { list -> list.firstOrNull { it.id == orgId } }

    override fun observeMemberRole(orgId: String, uid: String): Flow<MemberRole?> =
        members.map { list -> list.firstOrNull { it.uid == uid }?.role }

    override suspend fun createOrganization(
        profile: UserProfile,
        name: String,
        type: OrganizationType,
        address: String
    ): Result<String> = runCatching {
        val id = "org${organizations.value.size + 1}"
        organizations.update { it + Organization(id, name, type, address, profile.uid) }
        memberships.update { it + Membership(id, name, type, MemberRole.ADMIN) }
        id
    }

    override suspend fun leaveOrganization(uid: String, orgId: String): Result<Unit> = runCatching {
        memberships.update { list -> list.filterNot { it.orgId == orgId } }
    }

    override fun observeInvites(orgId: String): Flow<List<Invite>> = invites

    override suspend fun findInvite(code: String): Result<Invite?> = runCatching {
        invites.value.firstOrNull { it.code == code }
    }

    override suspend fun joinWithInvite(profile: UserProfile, invite: Invite): Result<Unit> = runCatching {
        memberships.update { it + Membership(invite.orgId, invite.orgName, invite.orgType, invite.role) }
    }

    override suspend fun createInvite(orgId: String, role: MemberRole): Result<String> = runCatching {
        val code = "NEU${invites.value.size + 1}"
        val org = MockData.organization
        invites.update { it + Invite(code, orgId, org.name, org.type, role) }
        code
    }

    override suspend fun deleteInvite(orgId: String, code: String): Result<Unit> = runCatching {
        invites.update { list -> list.filterNot { it.code == code } }
    }

    override suspend fun searchOrganizations(query: String): Result<List<Organization>> = runCatching {
        organizations.value.filter { it.name.contains(query, ignoreCase = true) }
    }

    override fun observeIsListed(orgId: String): Flow<Boolean> = listed

    override suspend fun setListed(orgId: String, listed: Boolean): Result<Unit> = runCatching {
        this.listed.update { listed }
    }

    override suspend fun sendJoinRequest(profile: UserProfile, organization: Organization): Result<Unit> = runCatching {
        val request = JoinRequest(
            orgId = organization.id,
            orgName = organization.name,
            orgType = organization.type,
            uid = profile.uid,
            firstName = profile.firstName,
            lastName = profile.lastName,
            email = profile.email
        )
        requests.update { it + request }
    }

    override fun observeMyJoinRequests(uid: String): Flow<List<JoinRequest>> =
        requests.map { list -> list.filter { it.uid == uid } }

    override suspend fun cancelJoinRequest(uid: String, orgId: String): Result<Unit> = runCatching {
        requests.update { list -> list.filterNot { it.uid == uid && it.orgId == orgId } }
    }

    override suspend fun completeAcceptedRequest(uid: String, request: JoinRequest): Result<Unit> = runCatching {
        memberships.update { it + Membership(request.orgId, request.orgName, request.orgType, MemberRole.PATIENT) }
    }

    override fun observeJoinRequests(orgId: String): Flow<List<JoinRequest>> =
        requests.map { list -> list.filter { it.orgId == orgId && it.status == JoinRequestStatus.PENDING } }

    override suspend fun acceptJoinRequest(request: JoinRequest): Result<Unit> = runCatching {
        setStatus(request, JoinRequestStatus.ACCEPTED)
    }

    override suspend fun declineJoinRequest(request: JoinRequest): Result<Unit> = runCatching {
        setStatus(request, JoinRequestStatus.DECLINED)
    }

    override fun observeMembers(orgId: String): Flow<List<Member>> = members

    override suspend fun updateMemberRole(orgId: String, uid: String, role: MemberRole): Result<Unit> = runCatching {
        members.update { list -> list.map { if (it.uid == uid) it.copy(role = role) else it } }
    }

    override suspend fun removeMember(orgId: String, uid: String): Result<Unit> = runCatching {
        members.update { list -> list.filterNot { it.uid == uid } }
    }

    private fun setStatus(request: JoinRequest, status: JoinRequestStatus) {
        requests.update { list ->
            list.map { if (it.uid == request.uid && it.orgId == request.orgId) it.copy(status = status) else it }
        }
    }
}
