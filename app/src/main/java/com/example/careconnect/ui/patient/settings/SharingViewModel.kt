package com.example.careconnect.ui.patient.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.R
import com.example.careconnect.domain.model.LinkedOrg
import com.example.careconnect.domain.model.MemberRole
import com.example.careconnect.domain.model.Membership
import com.example.careconnect.domain.model.OrganizationType
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.OrganizationRepository
import com.example.careconnect.domain.repository.PrescriptionRepository
import com.example.careconnect.util.ResourceHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Patient entscheidet, welche Praxis an welchen Pflegedienst Verordnungen schicken darf
@OptIn(ExperimentalCoroutinesApi::class)
class SharingViewModel(
    private val prescriptionRepo: PrescriptionRepository,
    authRepo: AuthRepository,
    orgRepo: OrganizationRepository
) : ViewModel() {

    private val uid = authRepo.currentUserId

    val state: StateFlow<SharingState> = (uid?.let { orgRepo.observeMemberships(it) } ?: flowOf(emptyList<Membership>()))
        .map { list -> list.filter { it.role == MemberRole.PATIENT } }
        .flatMapLatest { memberships ->
            val careServices = memberships.filter { it.orgType == OrganizationType.PFLEGEDIENST }
            val sources = memberships.filter { it.orgType != OrganizationType.PFLEGEDIENST }
            if (uid == null || sources.isEmpty() || careServices.isEmpty()) {
                flowOf(SharingState(careServices = careServices))
            } else {
                combine(sources.map { source ->
                    prescriptionRepo.observeLinkedOrgs(source.orgId, uid).map { links ->
                        SharingEntry(source, links.map { it.orgId }.toSet())
                    }
                }) { entries -> SharingState(entries.toList(), careServices) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SharingState())

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    fun toggle(entry: SharingEntry, careService: Membership) {
        val id = uid ?: return
        val careServices = state.value.careServices
        val newIds = if (careService.orgId in entry.linkedOrgIds) {
            entry.linkedOrgIds - careService.orgId
        } else {
            entry.linkedOrgIds + careService.orgId
        }
        val links = careServices
            .filter { it.orgId in newIds }
            .map { LinkedOrg(it.orgId, it.orgName) }

        viewModelScope.launch {
            prescriptionRepo.setLinkedOrgs(entry.source.orgId, id, links)
                .onFailure { e -> _message.update { ResourceHelper.getString(R.string.freigabe_konnte_nicht_gespeichert, e.message.orEmpty()) } }
        }
    }

    fun dismissMessage() = _message.update { null }
}
