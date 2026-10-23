package com.example.careconnect.ui.employee.organization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.R
import com.example.careconnect.domain.model.Invite
import com.example.careconnect.domain.model.JoinRequest
import com.example.careconnect.domain.model.Member
import com.example.careconnect.domain.model.MemberRole
import com.example.careconnect.domain.model.Organization
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.OrganizationRepository
import com.example.careconnect.session.OrgSession
import com.example.careconnect.util.ResourceHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Nur für die Leitung: Anfragen, Codes, Mitglieder, Sichtbarkeit
class ManageOrganizationViewModel(
    private val orgRepo: OrganizationRepository,
    authRepo: AuthRepository,
    session: OrgSession
) : ViewModel() {

    val currentUserId: String? = authRepo.currentUserId
    private val orgId: String? = session.orgId.value

    val organization: StateFlow<Organization?> = orgFlow(null) { orgRepo.observeOrganization(it) }
    val isListed: StateFlow<Boolean> = orgFlow(false) { orgRepo.observeIsListed(it) }
    val joinRequests: StateFlow<List<JoinRequest>> = orgFlow(emptyList()) { orgRepo.observeJoinRequests(it) }
    val invites: StateFlow<List<Invite>> = orgFlow(emptyList()) { orgRepo.observeInvites(it) }
    val members: StateFlow<List<Member>> = orgFlow(emptyList()) { orgRepo.observeMembers(it) }

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    fun setListed(listed: Boolean) = perform(ResourceHelper.getString(R.string.sichtbarkeit_konnte_nicht_geaendert)) { id ->
        orgRepo.setListed(id, listed)
    }

    fun accept(request: JoinRequest) = perform(ResourceHelper.getString(R.string.anfrage_konnte_nicht_angenommen)) {
        orgRepo.acceptJoinRequest(request)
    }

    fun decline(request: JoinRequest) = perform(ResourceHelper.getString(R.string.anfrage_konnte_nicht_abgelehnt)) {
        orgRepo.declineJoinRequest(request)
    }

    fun createInvite(role: MemberRole) = perform(ResourceHelper.getString(R.string.code_konnte_nicht_erstellt)) { id ->
        orgRepo.createInvite(id, role).map { }
    }

    fun deleteInvite(code: String) = perform(ResourceHelper.getString(R.string.code_konnte_nicht_geloescht)) { id ->
        orgRepo.deleteInvite(id, code)
    }

    fun changeRole(member: Member, role: MemberRole) = perform(ResourceHelper.getString(R.string.rolle_konnte_nicht_geaendert)) { id ->
        orgRepo.updateMemberRole(id, member.uid, role)
    }

    fun remove(member: Member) = perform(ResourceHelper.getString(R.string.mitglied_konnte_nicht_entfernt)) { id ->
        orgRepo.removeMember(id, member.uid)
    }

    fun dismissMessage() {
        _message.update { null }
    }

    private fun <T> orgFlow(empty: T, block: (String) -> Flow<T>): StateFlow<T> =
        (orgId?.let(block) ?: flowOf(empty))
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), empty)

    private fun perform(errorText: String, action: suspend (String) -> Result<Unit>) {
        val id = orgId ?: return
        viewModelScope.launch {
            action(id).onFailure { e -> _message.update { "$errorText: ${e.message}" } }
        }
    }
}
