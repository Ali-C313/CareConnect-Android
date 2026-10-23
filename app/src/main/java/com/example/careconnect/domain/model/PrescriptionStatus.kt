package com.example.careconnect.domain.model

import androidx.annotation.StringRes
import com.example.careconnect.R
import com.example.careconnect.util.ResourceHelper

enum class PrescriptionStatus(val value: String, @StringRes private val labelRes: Int) {
    OFFEN("offen", R.string.offen),
    ANGENOMMEN("angenommen", R.string.uebernommen),
    ABGELEHNT("abgelehnt", R.string.abgelehnt),
    ERLEDIGT("erledigt", R.string.erledigt);

    // Text in der gewählten Sprache
    val displayName: String
        get() = ResourceHelper.getString(labelRes)

    companion object {
        fun fromValue(value: String?): PrescriptionStatus =
            entries.firstOrNull { it.value == value } ?: OFFEN
    }
}
