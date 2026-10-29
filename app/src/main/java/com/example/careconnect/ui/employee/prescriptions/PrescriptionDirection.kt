package com.example.careconnect.ui.employee.prescriptions

import androidx.annotation.StringRes
import com.example.careconnect.R
import com.example.careconnect.util.ResourceHelper

// Eingang oder Ausgang bei den Verordnungen
enum class PrescriptionDirection(@StringRes private val labelRes: Int) {
    INCOMING(R.string.eingang),
    OUTGOING(R.string.ausgang);

    val displayName: String
        get() = ResourceHelper.getString(labelRes)
}
