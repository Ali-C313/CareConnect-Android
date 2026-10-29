package com.example.careconnect.domain.model

import androidx.annotation.StringRes
import com.example.careconnect.R
import com.example.careconnect.util.ResourceHelper

// Welche App der Nutzer sieht
enum class UserRole(@StringRes private val labelRes: Int) {
    EMPLOYEE(R.string.mitarbeiter),
    PATIENT(R.string.patient);

    val displayName: String
        get() = ResourceHelper.getString(labelRes)
}
