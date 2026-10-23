package com.example.careconnect.domain.model

import androidx.annotation.StringRes
import com.example.careconnect.R
import com.example.careconnect.util.ResourceHelper

// value = gleicher String wie in der iOS-App (Firestore-Feld "status"), bleibt deutsch
enum class StaffStatus(val value: String, @StringRes private val labelRes: Int) {
    AVAILABLE("Verfügbar", R.string.verfuegbar),
    BUSY("Beschäftigt", R.string.beschaeftigt),
    PAUSE("Pause", R.string.pause),
    OFF("Frei", R.string.frei),
    VACATION("Urlaub", R.string.urlaub);

    // Text in der gewählten Sprache
    val displayName: String
        get() = ResourceHelper.getString(labelRes)

    companion object {
        fun fromValue(value: String?): StaffStatus =
            entries.firstOrNull { it.value == value } ?: AVAILABLE
    }
}
