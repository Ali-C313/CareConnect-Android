package com.example.careconnect.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.careconnect.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Einstellungen im DataStore speichern und lesen
class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override val isDarkMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DARK_MODE] ?: false
    }

    override suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = enabled
        }
    }

    override val shareLocation: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SHARE_LOCATION]
    }

    override suspend fun setShareLocation(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHARE_LOCATION] = enabled
        }
    }

    override val showLiveLocation: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SHOW_LIVE_LOCATION]
    }

    override suspend fun setShowLiveLocation(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_LIVE_LOCATION] = enabled
        }
    }

    override val notificationsEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.NOTIFICATIONS] ?: true
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS] = enabled
        }
    }
}
