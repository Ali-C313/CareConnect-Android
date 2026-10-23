package com.example.careconnect

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.careconnect.domain.model.UserRole
import com.example.careconnect.ui.auth.AuthNavigation
import com.example.careconnect.ui.auth.AuthState
import com.example.careconnect.ui.auth.AuthViewModel
import com.example.careconnect.ui.components.LocationConsentDialog
import com.example.careconnect.ui.employee.EmployeeApp
import com.example.careconnect.ui.organization.OnboardingScreen
import com.example.careconnect.ui.patient.PatientApp
import org.koin.androidx.compose.koinViewModel

// Login -> Organisation wählen/gründen -> Mitarbeiter- oder Patienten-App
@Composable
fun AuthWrapper(
    authViewModel: AuthViewModel = koinViewModel()
) {
    val state by authViewModel.authState.collectAsState()
    val needsLocationConsent by authViewModel.needsLocationConsent.collectAsState()

    // Surface setzt die Textfarbe passend zu Hell/Dunkel, sonst ist Text ohne Farbe schwarz
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (val current = state) {
                is AuthState.Loading, is AuthState.Removed -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                is AuthState.LoggedIn -> {
                    // Beim Wechsel der Organisation alles neu aufbauen
                    key(current.membership.orgId) {
                        if (current.role == UserRole.EMPLOYEE) EmployeeApp(current.membership.orgType) else PatientApp()
                    }

                    if (needsLocationConsent) {
                        LocationConsentDialog(
                            onAllow = { authViewModel.setShareLocation(true) },
                            onDeny = { authViewModel.setShareLocation(false) }
                        )
                    }
                }

                is AuthState.NeedsOrganization -> {
                    OnboardingScreen(onLogout = authViewModel::logout)
                }

                else -> {
                    AuthNavigation(
                        isLoggedInWithoutProfile = current is AuthState.MissingProfile,
                        onLogout = authViewModel::logout
                    )
                }
            }
        }
    }
}
