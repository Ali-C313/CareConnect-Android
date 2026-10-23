package com.example.careconnect.domain.model

import androidx.annotation.StringRes
import com.example.careconnect.R
import com.example.careconnect.util.ResourceHelper

enum class MemberRole(val value: String, @StringRes private val labelRes: Int) {
    ADMIN("admin", R.string.leitung),
    STAFF("staff", R.string.mitarbeiter),
    PATIENT("patient", R.string.patient);

    // Text in der gewählten Sprache
    val displayName: String
        get() = ResourceHelper.getString(labelRes)

    // Leitung und Mitarbeiter sehen die Mitarbeiter-App
    val userRole: UserRole
        get() = if (this == PATIENT) UserRole.PATIENT else UserRole.EMPLOYEE

    companion object {
        fun fromValue(value: String?): MemberRole =
            entries.firstOrNull { it.value == value } ?: PATIENT
    }
}
