package com.example.careconnect.mock.repository

import com.example.careconnect.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

// Einstellungen im Speicher für die Previews
class SettingsRepositoryMock : SettingsRepository {

    private val _isDarkMode = MutableStateFlow(false)
    override val isDarkMode: Flow<Boolean> = _isDarkMode

    private val _shareLocation = MutableStateFlow<Boolean?>(true)
    override val shareLocation: Flow<Boolean?> = _shareLocation

    private val _showLiveLocation = MutableStateFlow<Boolean?>(true)
    override val showLiveLocation: Flow<Boolean?> = _showLiveLocation

    private val _notificationsEnabled = MutableStateFlow(true)
    override val notificationsEnabled: Flow<Boolean> = _notificationsEnabled

    override suspend fun setDarkMode(enabled: Boolean) = _isDarkMode.update { enabled }

    override suspend fun setShareLocation(enabled: Boolean) = _shareLocation.update { enabled }

    override suspend fun setShowLiveLocation(enabled: Boolean) = _showLiveLocation.update { enabled }

    override suspend fun setNotificationsEnabled(enabled: Boolean) = _notificationsEnabled.update { enabled }
}
