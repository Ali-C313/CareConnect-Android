package com.example.careconnect.ui.patient.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.mock.MockData
import com.example.careconnect.mock.device.LocationTrackerMock
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.EmployeeRepositoryMock
import com.example.careconnect.mock.repository.LocationRepositoryMock
import com.example.careconnect.mock.repository.OrganizationRepositoryMock
import com.example.careconnect.mock.repository.PatientRepositoryMock
import com.example.careconnect.mock.repository.PrescriptionRepositoryMock
import com.example.careconnect.mock.repository.SettingsRepositoryMock
import com.example.careconnect.mock.repository.UserRepositoryMock
import com.example.careconnect.service.TrackingManager
import com.example.careconnect.session.OrgSession
import com.example.careconnect.ui.settings.OrganizationSection
import com.example.careconnect.ui.settings.PrivacySection
import com.example.careconnect.ui.settings.SettingsContent
import com.example.careconnect.ui.settings.SettingsViewModel
import com.example.careconnect.ui.theme.CareConnectTheme
import org.koin.androidx.compose.koinViewModel

/** Einstellungen der Patienten */
@Composable
fun PatientSettingsScreen(
    onEditProfileClick: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
    sharingViewModel: SharingViewModel = koinViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val language by viewModel.language.collectAsState()

    SettingsContent(
        profile = profile,
        isDarkMode = isDarkMode,
        onDarkModeChange = viewModel::setDarkMode,
        language = language,
        onLanguageChange = viewModel::setLanguage,
        onEditProfileClick = onEditProfileClick,
        onLogout = viewModel::logout,
        organizationContent = {
            OrganizationSection(viewModel)
            SharingSection(sharingViewModel)
            PrivacySection(viewModel, isEmployee = false)
        }
    )
}

@Preview
@Composable
fun PatientSettingsScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        PatientSettingsScreen(
            onEditProfileClick = {},
            viewModel = SettingsViewModel(
                SettingsRepositoryMock(),
                AuthRepositoryMock(MockData.patient.id),
                TrackingManager(LocationTrackerMock(), LocationRepositoryMock()),
                UserRepositoryMock(),
                OrganizationRepositoryMock(),
                OrgSession(),
                EmployeeRepositoryMock(),
                PatientRepositoryMock()
            ),
            sharingViewModel = SharingViewModel(
                PrescriptionRepositoryMock(),
                AuthRepositoryMock(MockData.patient.id),
                OrganizationRepositoryMock()
            )
        )
    }
}
