package com.example.careconnect.ui.employee.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.mock.platform.LocationTrackerMock
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.EmployeeRepositoryMock
import com.example.careconnect.mock.repository.LocationRepositoryMock
import com.example.careconnect.mock.repository.OrganizationRepositoryMock
import com.example.careconnect.mock.repository.PatientRepositoryMock
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

@Composable
fun EmployeeSettingsScreen(
    onEditProfileClick: () -> Unit,
    onManageOrganizationClick: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
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
            OrganizationSection(viewModel, onManageOrganizationClick)
            PrivacySection(viewModel, isEmployee = true)
        }
    )
}

@Preview
@Composable
fun EmployeeSettingsScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        EmployeeSettingsScreen(
            onEditProfileClick = {},
            onManageOrganizationClick = {},
            viewModel = SettingsViewModel(
                SettingsRepositoryMock(),
                AuthRepositoryMock(),
                TrackingManager(LocationTrackerMock(), LocationRepositoryMock()),
                UserRepositoryMock(),
                OrganizationRepositoryMock(),
                OrgSession(),
                EmployeeRepositoryMock(),
                PatientRepositoryMock()
            )
        )
    }
}
