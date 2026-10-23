package com.example.careconnect.ui.auth

import com.example.careconnect.domain.model.Membership
import com.example.careconnect.domain.model.UserProfile
import com.example.careconnect.domain.model.UserRole

sealed class AuthState {
    object Loading : AuthState()
    object LoggedOut : AuthState()

    // Konto da, aber noch kein Profil unter users/{uid} (z.B. Registrierung läuft noch)
    data class MissingProfile(val uid: String) : AuthState()

    // Aus der Organisation entfernt, Eintrag wird gerade aufgeräumt
    data class Removed(val uid: String, val orgId: String) : AuthState()

    // Noch keiner Organisation beigetreten
    data class NeedsOrganization(val uid: String, val profile: UserProfile) : AuthState()

    data class LoggedIn(
        val uid: String,
        val profile: UserProfile,
        val membership: Membership,
        val memberships: List<Membership>
    ) : AuthState() {
        val role: UserRole
            get() = membership.role.userRole
    }
}
