package com.example.careconnect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.careconnect.ui.settings.SettingsViewModel
import com.example.careconnect.ui.theme.CareConnectTheme
import org.koin.androidx.compose.koinViewModel

// Arabisch (rechts nach links) stellt Android über android:supportsRtl selbst um
@Composable
fun App() {
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

    CareConnectTheme(darkTheme = isDarkMode) {
        AuthWrapper()
    }
}
