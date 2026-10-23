package com.example.careconnect.domain.model

import androidx.annotation.StringRes
import com.example.careconnect.R
import com.example.careconnect.util.ResourceHelper

enum class OrderState(val value: String, @StringRes private val labelRes: Int) {
    OFFEN("offen", R.string.offen),
    AKTUELL("aktuell", R.string.aktuell),
    ERLEDIGT("erledigt", R.string.erledigt);

    // Text in der gewählten Sprache
    val displayName: String
        get() = ResourceHelper.getString(labelRes)

    companion object {
        fun fromValue(value: String?): OrderState =
            entries.firstOrNull { it.value == value?.lowercase() } ?: OFFEN
    }
}
