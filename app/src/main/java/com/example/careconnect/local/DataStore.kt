package com.example.careconnect.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// Einstellungen der App (Dunkelmodus, Sprache, Standort, ...)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "careconnect")
