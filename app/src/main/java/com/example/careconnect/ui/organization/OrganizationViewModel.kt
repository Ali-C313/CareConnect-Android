package com.example.careconnect.ui.organization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.R
import com.example.careconnect.domain.model.JoinRequest
import com.example.careconnect.domain.model.Organization
import com.example.careconnect.domain.model.OrganizationType
import com.example.careconnect.domain.model.UserProfile
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.OrganizationRepository
import com.example.careconnect.domain.repository.UserRepository
import com.example.careconnect.util.ResourceHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Organisation per Code beitreten oder neu gründen
class OrganizationViewModel(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository,
    private val orgRepo: OrganizationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrganizationUiState())
    val uiState = _uiState.asStateFlow()

    // Eigene offene oder abgelehnte Anfragen
    val myRequests: StateFlow<List<JoinRequest>> =
        (authRepo.currentUserId?.let { orgRepo.observeMyJoinRequests(it) } ?: flowOf(emptyList<JoinRequest>()))
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var searchJob: Job? = null

    fun onSearchChange(value: String) {
        _uiState.update { it.copy(searchQuery = value, errorMessage = null) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            _uiState.update { it.copy(isSearching = true) }
            val results = orgRepo.searchOrganizations(value).getOrDefault(emptyList())
            _uiState.update { it.copy(searchResults = results, isSearching = false) }
        }
    }

    // Als Patient bei einer gefundenen Organisation anfragen
    fun sendRequest(organization: Organization) {
        launchWithProfile { profile ->
            val memberships = orgRepo.observeMemberships(profile.uid).first()
            when {
                memberships.any { it.orgId == organization.id } -> ResourceHelper.getString(R.string.bist_dort_schon_mitglied)
                myRequests.value.any { it.orgId == organization.id } -> ResourceHelper.getString(R.string.hast_dort_schon_angefragt)
                else -> orgRepo.sendJoinRequest(profile, organization)
                    .exceptionOrNull()
                    ?.let { ResourceHelper.getString(R.string.anfrage_fehlgeschlagen, it.message.orEmpty()) }
            }
        }
    }

    fun cancelRequest(orgId: String) {
        val uid = authRepo.currentUserId ?: return
        viewModelScope.launch {
            orgRepo.cancelJoinRequest(uid, orgId)
        }
    }

    fun onCodeChange(value: String) = _uiState.update { it.copy(code = value.uppercase(), errorMessage = null) }
    fun onOrgNameChange(value: String) = _uiState.update { it.copy(orgName = value, errorMessage = null) }
    fun onOrgTypeChange(value: OrganizationType) = _uiState.update { it.copy(orgType = value) }
    fun onOrgAddressChange(value: String) = _uiState.update { it.copy(orgAddress = value) }

    fun join() {
        val state = _uiState.value
        if (state.code.isBlank()) {
            _uiState.update { it.copy(errorMessage = ResourceHelper.getString(R.string.einladungscode_eingeben)) }
            return
        }

        launchWithProfile { profile ->
            val invite = orgRepo.findInvite(state.code).getOrNull()
                ?: return@launchWithProfile ResourceHelper.getString(R.string.einladungscode_ungueltig)

            // Schon Mitglied -> nur dorthin wechseln
            val memberships = orgRepo.observeMemberships(profile.uid).first()
            val result = if (memberships.any { it.orgId == invite.orgId }) {
                userRepo.setActiveOrg(profile.uid, invite.orgId)
            } else {
                orgRepo.joinWithInvite(profile, invite)
            }
            result.exceptionOrNull()?.let { ResourceHelper.getString(R.string.beitritt_fehlgeschlagen, it.message.orEmpty()) }
        }
    }

    fun create() {
        val state = _uiState.value
        if (state.orgName.isBlank()) {
            _uiState.update { it.copy(errorMessage = ResourceHelper.getString(R.string.namen_organisation_eingeben)) }
            return
        }

        launchWithProfile { profile ->
            orgRepo.createOrganization(profile, state.orgName.trim(), state.orgType, state.orgAddress.trim())
                .exceptionOrNull()
                ?.let { ResourceHelper.getString(R.string.organisation_konnte_nicht_angelegt, it.message.orEmpty()) }
        }
    }

    // action liefert eine Fehlermeldung oder null bei Erfolg
    private fun launchWithProfile(action: suspend (UserProfile) -> String?) {
        val uid = authRepo.currentUserId ?: return
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val profile = userRepo.getProfile(uid).getOrNull()
            val error = if (profile == null) ResourceHelper.getString(R.string.profil_wurde_nicht_gefunden) else action(profile)
            _uiState.update {
                if (error == null) OrganizationUiState() else it.copy(isLoading = false, errorMessage = error)
            }
        }
    }
}
