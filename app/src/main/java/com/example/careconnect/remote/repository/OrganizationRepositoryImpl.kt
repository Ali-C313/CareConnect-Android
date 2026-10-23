package com.example.careconnect.remote.repository

import com.example.careconnect.domain.model.Invite
import com.example.careconnect.domain.model.JoinRequest
import com.example.careconnect.domain.model.JoinRequestStatus
import com.example.careconnect.domain.model.Member
import com.example.careconnect.domain.model.MemberRole
import com.example.careconnect.domain.model.Membership
import com.example.careconnect.domain.model.Organization
import com.example.careconnect.domain.model.OrganizationType
import com.example.careconnect.domain.model.StaffStatus
import com.example.careconnect.domain.model.UserProfile
import com.example.careconnect.domain.repository.OrganizationRepository
import com.example.careconnect.remote.CollectionNames
import com.example.careconnect.remote.listenAsFlow
import com.example.careconnect.remote.model.DirectoryDto
import com.example.careconnect.remote.model.EmployeeDto
import com.example.careconnect.remote.model.InviteDto
import com.example.careconnect.remote.model.JoinRequestDto
import com.example.careconnect.remote.model.JoinRequestRefDto
import com.example.careconnect.remote.model.MemberDto
import com.example.careconnect.remote.model.MembershipDto
import com.example.careconnect.remote.model.OrganizationDto
import com.example.careconnect.remote.model.PatientDto
import com.example.careconnect.remote.model.toInvite
import com.example.careconnect.remote.model.toJoinRequest
import com.example.careconnect.remote.model.toMember
import com.example.careconnect.remote.model.toMembership
import com.example.careconnect.remote.model.toOrganization
import com.example.careconnect.remote.model.toTimestamp
import com.example.careconnect.remote.org
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class OrganizationRepositoryImpl(
    private val db: FirebaseFirestore
) : OrganizationRepository {

    private fun userRef(uid: String) = db.collection(CollectionNames.USERS).document(uid)
    private fun inviteRef(code: String) = db.collection(CollectionNames.INVITES).document(code)
    private fun directoryRef(orgId: String) = db.collection(CollectionNames.DIRECTORY).document(orgId)
    private fun memberRef(orgId: String, uid: String) = db.org(orgId).collection(CollectionNames.MEMBERS).document(uid)
    private fun requestRef(orgId: String, uid: String) = db.org(orgId).collection(CollectionNames.JOIN_REQUESTS).document(uid)
    private fun myRequestRef(uid: String, orgId: String) = userRef(uid).collection(CollectionNames.JOIN_REQUESTS).document(orgId)
    private fun membershipRef(uid: String, orgId: String) = userRef(uid).collection(CollectionNames.MEMBERSHIPS).document(orgId)

    override fun observeMemberships(uid: String): Flow<List<Membership>> =
        userRef(uid).collection(CollectionNames.MEMBERSHIPS).listenAsFlow()
            .map { snapshot ->
                snapshot.documents
                    .mapNotNull { doc -> runCatching { doc.toObject<MembershipDto>()?.toMembership(doc.id) }.getOrNull() }
                    .sortedBy { it.orgName }
            }
            .catch { emit(emptyList()) }

    override fun observeOrganization(orgId: String): Flow<Organization?> = db.org(orgId).listenAsFlow()
        .map { doc ->
            if (!doc.exists()) null
            else runCatching { doc.toObject<OrganizationDto>()?.toOrganization(doc.id) }.getOrNull()
        }
        .catch { emit(null) }

    // Kopie der Codes unter der Organisation, damit die Leitung sie auflisten kann
    override fun observeInvites(orgId: String): Flow<List<Invite>> =
        db.org(orgId).collection(CollectionNames.INVITES).listenAsFlow()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    runCatching { doc.toObject<InviteDto>()?.toInvite(doc.id) }.getOrNull()
                }
            }
            .catch { emit(emptyList()) }

    override suspend fun createOrganization(
        profile: UserProfile,
        name: String,
        type: OrganizationType,
        address: String
    ): Result<String> = runCatching {
        val orgId = UUID.randomUUID().toString()
        val orgRef = db.org(orgId)
        val batch = db.batch()

        batch.set(orgRef, OrganizationDto(name, type.value, address, profile.uid, Timestamp.now()))
        addMember(batch, orgId, name, type, profile, MemberRole.ADMIN, inviteCode = null)

        // In der Suche sichtbar, kann die Leitung später ausschalten
        batch.set(directoryRef(orgId), DirectoryDto(name, name.lowercase(), type.value, address))

        // Je ein Code für Mitarbeiter und Patienten
        addInvite(batch, orgId, name, type, MemberRole.STAFF, profile.uid)
        addInvite(batch, orgId, name, type, MemberRole.PATIENT, profile.uid)

        batch.commit().await()
        orgId
    }

    // Fehler (z.B. alte Regeln) zählen nicht als "entfernt", sonst würde die Mitgliedschaft gelöscht
    override fun observeMemberRole(orgId: String, uid: String): Flow<MemberRole?> = memberRef(orgId, uid).listenAsFlow()
        .map { doc ->
            if (!doc.exists()) null
            else runCatching { MemberRole.fromValue(doc.getString("role")) }.getOrNull()
        }
        .catch { println("Mitgliedschaft konnte nicht geladen werden: ${it.message}") }

    override suspend fun leaveOrganization(uid: String, orgId: String): Result<Unit> = runCatching {
        val batch = db.batch()
        batch.delete(memberRef(orgId, uid))
        batch.delete(membershipRef(uid, orgId))
        batch.commit().await()
        Unit
    }

    override suspend fun searchOrganizations(query: String): Result<List<Organization>> = runCatching {
        val search = query.trim().lowercase()
        if (search.length < 2) return@runCatching emptyList()
        // Wenige Einträge, deshalb reicht Filtern in der App
        db.collection(CollectionNames.DIRECTORY).get().await().documents
            .mapNotNull { doc -> runCatching { doc.toObject<DirectoryDto>()?.toOrganization(doc.id) }.getOrNull() }
            .filter { it.name.lowercase().contains(search) || it.address.lowercase().contains(search) }
            .sortedBy { it.name }
            .take(20)
    }

    override fun observeIsListed(orgId: String): Flow<Boolean> = directoryRef(orgId).listenAsFlow()
        .map { it.exists() }
        .catch { emit(false) }

    override suspend fun setListed(orgId: String, listed: Boolean): Result<Unit> = runCatching {
        if (listed) {
            val org = db.org(orgId).get().await().toObject<OrganizationDto>() ?: OrganizationDto()
            val name = org.name ?: ""
            directoryRef(orgId).set(DirectoryDto(name, name.lowercase(), org.type, org.address)).await()
        } else {
            directoryRef(orgId).delete().await()
        }
        Unit
    }

    override suspend fun sendJoinRequest(profile: UserProfile, organization: Organization): Result<Unit> = runCatching {
        val now = Timestamp.now()
        val batch = db.batch()
        batch.set(
            requestRef(organization.id, profile.uid),
            JoinRequestDto(
                orgId = organization.id,
                orgName = organization.name,
                orgType = organization.type.value,
                firstName = profile.firstName,
                lastName = profile.lastName,
                email = profile.email,
                address = profile.address,
                city = profile.city,
                phone = profile.phone,
                birthDate = profile.birthDate?.toTimestamp(),
                profileImageURL = profile.profileImageUrl,
                status = JoinRequestStatus.PENDING.value,
                createdAt = now
            )
        )
        batch.set(myRequestRef(profile.uid, organization.id), JoinRequestRefDto(organization.id, now))
        batch.commit().await()
        Unit
    }

    // Eigene Anfragen: IDs beim Nutzer, Status bei der Organisation
    override fun observeMyJoinRequests(uid: String): Flow<List<JoinRequest>> =
        userRef(uid).collection(CollectionNames.JOIN_REQUESTS).listenAsFlow()
            .map { snapshot -> snapshot.documents.map { it.id } }
            .flatMapLatest { orgIds ->
                if (orgIds.isEmpty()) {
                    flowOf(emptyList<JoinRequest>())
                } else {
                    combine(orgIds.map { orgId -> observeRequest(orgId, uid) }) { requests ->
                        requests.filterNotNull()
                    }
                }
            }
            .catch { emit(emptyList()) }

    private fun observeRequest(orgId: String, uid: String): Flow<JoinRequest?> = requestRef(orgId, uid).listenAsFlow()
        .map { doc ->
            if (!doc.exists()) null
            else runCatching { doc.toObject<JoinRequestDto>()?.toJoinRequest(doc.id) }.getOrNull()
        }
        .catch { emit(null) }

    override suspend fun cancelJoinRequest(uid: String, orgId: String): Result<Unit> = runCatching {
        val batch = db.batch()
        batch.delete(requestRef(orgId, uid))
        batch.delete(myRequestRef(uid, orgId))
        batch.commit().await()
        Unit
    }

    // Leitung hat angenommen -> Mitgliedschaft beim Nutzer eintragen und Anfrage aufräumen
    override suspend fun completeAcceptedRequest(uid: String, request: JoinRequest): Result<Unit> = runCatching {
        val batch = db.batch()
        batch.set(
            membershipRef(uid, request.orgId),
            MembershipDto(request.orgName, request.orgType.value, MemberRole.PATIENT.value, Timestamp.now())
        )
        batch.update(userRef(uid), "activeOrgId", request.orgId)
        batch.delete(requestRef(request.orgId, uid))
        batch.delete(myRequestRef(uid, request.orgId))
        batch.commit().await()
        Unit
    }

    override fun observeJoinRequests(orgId: String): Flow<List<JoinRequest>> =
        db.org(orgId).collection(CollectionNames.JOIN_REQUESTS).listenAsFlow()
            .map { snapshot ->
                snapshot.documents
                    .mapNotNull { doc -> runCatching { doc.toObject<JoinRequestDto>()?.toJoinRequest(doc.id) }.getOrNull() }
                    .filter { it.status == JoinRequestStatus.PENDING }
                    .sortedBy { it.createdAt ?: 0L }
            }
            .catch { emit(emptyList()) }

    override suspend fun acceptJoinRequest(request: JoinRequest): Result<Unit> = runCatching {
        val now = Timestamp.now()
        val orgRef = db.org(request.orgId)
        val batch = db.batch()
        batch.set(
            memberRef(request.orgId, request.uid),
            MemberDto(MemberRole.PATIENT.value, request.firstName, request.lastName, null, now)
        )
        batch.set(
            orgRef.collection(CollectionNames.PATIENTS).document(request.uid),
            PatientDto(
                uid = request.uid,
                firstName = request.firstName,
                lastName = request.lastName,
                address = request.address,
                city = request.city,
                phone = request.phone,
                email = request.email,
                birthDate = request.birthDate?.toTimestamp(),
                profileImageURL = request.profileImageUrl,
                createdAt = now
            ),
            SetOptions.merge()
        )
        batch.update(requestRef(request.orgId, request.uid), "status", JoinRequestStatus.ACCEPTED.value)
        batch.commit().await()
        Unit
    }

    override suspend fun declineJoinRequest(request: JoinRequest): Result<Unit> = runCatching {
        requestRef(request.orgId, request.uid).update("status", JoinRequestStatus.DECLINED.value).await()
        Unit
    }

    override fun observeMembers(orgId: String): Flow<List<Member>> =
        db.org(orgId).collection(CollectionNames.MEMBERS).listenAsFlow()
            .map { snapshot ->
                snapshot.documents
                    .mapNotNull { doc -> runCatching { doc.toObject<MemberDto>()?.toMember(doc.id) }.getOrNull() }
                    .sortedWith(compareBy({ it.role.ordinal }, { it.name }))
            }
            .catch { emit(emptyList()) }

    override suspend fun updateMemberRole(orgId: String, uid: String, role: MemberRole): Result<Unit> = runCatching {
        memberRef(orgId, uid).update("role", role.value).await()
        Unit
    }

    override suspend fun removeMember(orgId: String, uid: String): Result<Unit> = runCatching {
        memberRef(orgId, uid).delete().await()
        Unit
    }

    override suspend fun findInvite(code: String): Result<Invite?> = runCatching {
        val cleaned = code.trim().uppercase()
        if (cleaned.isEmpty()) return@runCatching null
        val doc = inviteRef(cleaned).get().await()
        if (!doc.exists()) null else doc.toObject<InviteDto>()?.toInvite(doc.id)
    }

    override suspend fun joinWithInvite(profile: UserProfile, invite: Invite): Result<Unit> = runCatching {
        val batch = db.batch()
        addMember(batch, invite.orgId, invite.orgName, invite.orgType, profile, invite.role, invite.code)
        batch.commit().await()
        Unit
    }

    override suspend fun createInvite(orgId: String, role: MemberRole): Result<String> = runCatching {
        val org = db.org(orgId).get().await().toObject<OrganizationDto>() ?: OrganizationDto()
        val batch = db.batch()
        val code = addInvite(
            batch,
            orgId,
            org.name ?: "",
            OrganizationType.fromValue(org.type),
            role,
            createdBy = org.ownerId ?: ""
        )
        batch.commit().await()
        code
    }

    override suspend fun deleteInvite(orgId: String, code: String): Result<Unit> = runCatching {
        val batch = db.batch()
        batch.delete(inviteRef(code))
        batch.delete(db.org(orgId).collection(CollectionNames.INVITES).document(code))
        batch.commit().await()
        Unit
    }

    // Mitglied + Profil in der Organisation + Eintrag beim Nutzer
    private fun addMember(
        batch: WriteBatch,
        orgId: String,
        orgName: String,
        orgType: OrganizationType,
        profile: UserProfile,
        role: MemberRole,
        inviteCode: String?
    ) {
        val orgRef = db.org(orgId)
        val now = Timestamp.now()

        batch.set(
            memberRef(orgId, profile.uid),
            MemberDto(role.value, profile.firstName, profile.lastName, inviteCode, now)
        )

        if (role == MemberRole.PATIENT) {
            batch.set(
                orgRef.collection(CollectionNames.PATIENTS).document(profile.uid),
                PatientDto(
                    uid = profile.uid,
                    firstName = profile.firstName,
                    lastName = profile.lastName,
                    address = profile.address,
                    city = profile.city,
                    phone = profile.phone,
                    email = profile.email,
                    birthDate = profile.birthDate?.toTimestamp(),
                    profileImageURL = profile.profileImageUrl,
                    createdAt = now
                ),
                SetOptions.merge()
            )
        } else {
            batch.set(
                orgRef.collection(CollectionNames.EMPLOYEES).document(profile.uid),
                EmployeeDto(
                    uid = profile.uid,
                    firstName = profile.firstName,
                    lastName = profile.lastName,
                    role = if (role == MemberRole.ADMIN) "Leitung" else "Pflegekraft",
                    status = StaffStatus.AVAILABLE.value,
                    email = profile.email,
                    birthDate = profile.birthDate?.toTimestamp(),
                    profileImageURL = profile.profileImageUrl,
                    inviteCode = inviteCode
                ),
                SetOptions.merge()
            )
        }

        batch.set(
            membershipRef(profile.uid, orgId),
            MembershipDto(orgName, orgType.value, role.value, now)
        )
        batch.update(userRef(profile.uid), "activeOrgId", orgId)
    }

    /** @return der neue Code */
    private fun addInvite(
        batch: WriteBatch,
        orgId: String,
        orgName: String,
        orgType: OrganizationType,
        role: MemberRole,
        createdBy: String
    ): String {
        val code = generateCode()
        val dto = InviteDto(orgId, orgName, orgType.value, role.value, createdBy, Timestamp.now())
        batch.set(inviteRef(code), dto)
        batch.set(db.org(orgId).collection(CollectionNames.INVITES).document(code), dto)
        return code
    }

    // 8 Zeichen ohne leicht verwechselbare wie 0/O oder 1/I
    private fun generateCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..8).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    }
}
