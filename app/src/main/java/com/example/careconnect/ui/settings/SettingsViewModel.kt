package com.example.careconnect.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.R
import com.example.careconnect.domain.model.MemberRole
import com.example.careconnect.domain.model.Membership
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.EmployeeRepository
import com.example.careconnect.domain.repository.OrganizationRepository
import com.example.careconnect.domain.repository.PatientRepository
import com.example.careconnect.domain.repository.SettingsRepository
import com.example.careconnect.domain.repository.UserRepository
import com.example.careconnect.i18n.AppLanguage
import com.example.careconnect.platform.deviceLanguage
import com.example.careconnect.service.TrackingManager
import com.example.careconnect.session.OrgSession
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

// Wird von beiden Rollen genutzt (Dunkelmodus, Profil-Kopf, Organisation, Abmelden)
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModel(
    private val settingsRepo: SettingsRepository,
    private val authRepo: AuthRepository,
    private val trackingManager: TrackingManager,
    private val userRepo: UserRepository,
    private val orgRepo: OrganizationRepository,
    session: OrgSession,
    employeeRepo: EmployeeRepository,
    patientRepo: PatientRepository
) : ViewModel() {

    private val uid = authRepo.currentUserId

    val isDarkMode = settingsRepo.isDarkMode
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    val profile = combine(
        if (uid != null) employeeRepo.getEmployee(uid) else flowOf(null),
        if (uid != null) patientRepo.getPatient(uid) else flowOf(null)
    ) { employee, patient ->
        val email = authRepo.currentUserEmail ?: "—"
        when {
            employee != null -> ProfileInfo(employee.name.ifEmpty { ResourceHelper.getString(R.string.unbekannt) }, email, employee.profileImageUrl)
            patient != null -> ProfileInfo(patient.name, email, patient.profileImageUrl)
            else -> ProfileInfo(email = email)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ProfileInfo()
    )

    val memberships: StateFlow<List<Membership>> =
        (if (uid != null) orgRepo.observeMemberships(uid) else flowOf(emptyList()))
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Rolle direkt aus der Organisation, falls die Leitung sie geändert hat
    val currentMembership: StateFlow<Membership?> = combine(memberships, session.orgId) { list, orgId ->
        list.firstOrNull { it.orgId == orgId }
    }.flatMapLatest { membership ->
        if (membership == null || uid == null) flowOf(membership)
        else orgRepo.observeMemberRole(membership.orgId, uid).map { role ->
            role?.let { membership.copy(role = it) } ?: membership
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    val notificationsEnabled = settingsRepo.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val shareLocation = settingsRepo.shareLocation
        .map { it == true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val showLiveLocation = settingsRepo.showLiveLocation
        .map { it == true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Gewählte App-Sprache, sonst die vom Handy
    private val _language = MutableStateFlow(
        AppLanguage.fromCode(AppCompatDelegate.getApplicationLocales()[0]?.language ?: deviceLanguage())
    )
    val language = _language.asStateFlow()

    // Android speichert die Sprache selbst und baut die Activity neu auf
    fun setLanguage(language: AppLanguage) {
        _language.update { language }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.code))
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setNotificationsEnabled(enabled) }
    }

    fun setShareLocation(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setShareLocation(enabled) }
    }

    fun setShowLiveLocation(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setShowLiveLocation(enabled) }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.setDarkMode(enabled)
        }
    }

    fun switchOrganization(orgId: String) {
        val id = uid ?: return
        viewModelScope.launch {
            userRepo.setActiveOrg(id, orgId)
                .onFailure { e -> _message.update { ResourceHelper.getString(R.string.wechsel_fehlgeschlagen, e.message.orEmpty()) } }
        }
    }

    // Leitung kann nicht austreten, sonst hätte die Organisation niemanden mehr
    fun leaveOrganization() {
        val id = uid ?: return
        val membership = currentMembership.value ?: return
        if (membership.role == MemberRole.ADMIN) {
            _message.update { ResourceHelper.getString(R.string.leitung_kann_nicht_austreten) }
            return
        }
        viewModelScope.launch {
            orgRepo.leaveOrganization(id, membership.orgId)
                .onFailure { e -> _message.update { ResourceHelper.getString(R.string.austreten_fehlgeschlagen, e.message.orEmpty()) } }
        }
    }

    fun dismissMessage() {
        _message.update { null }
    }

    fun logout() {
        viewModelScope.launch {
            trackingManager.stop()
            authRepo.logout()
        }
    }
}
