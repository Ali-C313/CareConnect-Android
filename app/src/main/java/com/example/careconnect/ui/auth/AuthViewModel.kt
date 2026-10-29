package com.example.careconnect.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.domain.model.JoinRequest
import com.example.careconnect.domain.model.JoinRequestStatus
import com.example.careconnect.domain.model.UserRole
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.OrderRepository
import com.example.careconnect.domain.repository.OrganizationRepository
import com.example.careconnect.domain.repository.SettingsRepository
import com.example.careconnect.domain.repository.UserRepository
import com.example.careconnect.service.NotificationWatcher
import com.example.careconnect.service.TrackingManager
import com.example.careconnect.session.OrgSession
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModel(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository,
    private val orgRepo: OrganizationRepository,
    private val orderRepo: OrderRepository,
    private val trackingManager: TrackingManager,
    private val session: OrgSession,
    private val settingsRepo: SettingsRepository,
    private val notificationWatcher: NotificationWatcher
) : ViewModel() {

    // Für die Standort-Abfrage, sobald ein Einsatz läuft
    val isTracking: StateFlow<Boolean> = trackingManager.isTracking

    // Nach erteilter Standort-Berechtigung neu starten
    fun restartTracking() = trackingManager.restart()

    val authState: StateFlow<AuthState> = authRepo.authState
        .flatMapLatest { uid ->
            if (uid == null) flowOf<AuthState>(AuthState.LoggedOut)
            else userRepo.observeProfile(uid).flatMapLatest { profile ->
                if (profile == null) flowOf<AuthState>(AuthState.MissingProfile(uid))
                else orgRepo.observeMemberships(uid).flatMapLatest { memberships ->
                    // Zuletzt gewählte Organisation, sonst die erste
                    val active = memberships.firstOrNull { it.orgId == profile.activeOrgId }
                        ?: memberships.firstOrNull()
                    if (active == null) {
                        flowOf<AuthState>(AuthState.NeedsOrganization(uid, profile))
                    } else {
                        // Die Rolle zählt so, wie sie in der Organisation steht (Leitung kann sie ändern)
                        orgRepo.observeMemberRole(active.orgId, uid).map { role ->
                            if (role == null) AuthState.Removed(uid, active.orgId)
                            else AuthState.LoggedIn(uid, profile, active.copy(role = role), memberships)
                        }
                    }
                }
            }
        }
        .distinctUntilChanged()
        .onEach { state ->
            session.set((state as? AuthState.LoggedIn)?.membership?.orgId)
            if (state is AuthState.Removed) {
                viewModelScope.launch { orgRepo.leaveOrganization(state.uid, state.orgId) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AuthState.Loading)

    // Mitarbeiter-ID, solange ein Auftrag angenommen ist
    private val activeEmployee: StateFlow<String?> = authState
        .flatMapLatest { state ->
            if (state is AuthState.LoggedIn && state.role == UserRole.EMPLOYEE) {
                orderRepo.hasActiveOrder(state.uid).map { active -> if (active) state.uid else null }
            } else {
                flowOf<String?>(null)
            }
        }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Einmal fragen, bevor der Standort zum ersten Mal geteilt wird
    val needsLocationConsent: StateFlow<Boolean> = combine(activeEmployee, settingsRepo.shareLocation) { id, share ->
        id != null && share == null
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        observeTracking()
        observeNotifications()
        completeAcceptedRequests()
    }

    // Angenommene Beitrittsanfragen in eine Mitgliedschaft umwandeln
    private fun completeAcceptedRequests() {
        viewModelScope.launch {
            authRepo.authState
                .flatMapLatest { uid ->
                    if (uid == null) flowOf(emptyList<Pair<String, JoinRequest>>())
                    else orgRepo.observeMyJoinRequests(uid).map { list ->
                        list.filter { it.status == JoinRequestStatus.ACCEPTED }.map { uid to it }
                    }
                }
                .collect { accepted ->
                    accepted.forEach { (uid, request) -> orgRepo.completeAcceptedRequest(uid, request) }
                }
        }
    }

    // Standort nur teilen, solange ein Auftrag angenommen ist und der Mitarbeiter zugestimmt hat
    private fun observeTracking() {
        viewModelScope.launch {
            combine(activeEmployee, settingsRepo.shareLocation) { id, share -> if (share == true) id else null }
                .distinctUntilChanged()
                .collect { employeeId ->
                    if (employeeId != null) trackingManager.start(employeeId) else trackingManager.stop()
                }
        }
    }

    fun setShareLocation(allowed: Boolean) {
        viewModelScope.launch { settingsRepo.setShareLocation(allowed) }
    }

    // Benachrichtigungen für die aktuelle Organisation, bei Wechsel oder Logout neu
    private fun observeNotifications() {
        viewModelScope.launch {
            authState
                .map { state ->
                    (state as? AuthState.LoggedIn)?.let {
                        Triple(it.uid, it.membership.orgId, it.role == UserRole.EMPLOYEE)
                    }
                }
                .distinctUntilChanged()
                .collectLatest { target ->
                    val (uid, orgId, isEmployee) = target ?: return@collectLatest
                    try {
                        notificationWatcher.watch(uid, orgId, isEmployee)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Log.e("AuthViewModel", "Benachrichtigungen: ${e.message}")
                    }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            trackingManager.stop()
            authRepo.logout()
        }
    }
}
