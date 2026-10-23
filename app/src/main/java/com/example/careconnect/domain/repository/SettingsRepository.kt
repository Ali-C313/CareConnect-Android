package com.example.careconnect.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val isDarkMode: Flow<Boolean>
    suspend fun setDarkMode(enabled: Boolean)

    // Mitarbeiter: Standort bei Einsätzen teilen (null = noch nicht gefragt)
    val shareLocation: Flow<Boolean?>
    suspend fun setShareLocation(enabled: Boolean)

    // Patient: Live-Standort des Mitarbeiters anzeigen (null = noch nicht gefragt)
    val showLiveLocation: Flow<Boolean?>
    suspend fun setShowLiveLocation(enabled: Boolean)

    val notificationsEnabled: Flow<Boolean>
    suspend fun setNotificationsEnabled(enabled: Boolean)
}
