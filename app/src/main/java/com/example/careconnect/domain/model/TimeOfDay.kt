package com.example.careconnect.domain.model

import androidx.annotation.StringRes
import com.example.careconnect.R
import com.example.careconnect.util.ResourceHelper

// value wird so in Firestore gespeichert und bleibt deutsch
enum class TimeOfDay(val value: String, @StringRes private val labelRes: Int) {
    MORNING("Morgens", R.string.morgens),
    NOON("Mittags", R.string.mittags),
    EVENING("Abends", R.string.abends);

    // Text in der gewählten Sprache
    val displayName: String
        get() = ResourceHelper.getString(labelRes)

    companion object {
        fun fromValue(value: String?): TimeOfDay =
            entries.firstOrNull { it.value == value } ?: MORNING
    }
}
