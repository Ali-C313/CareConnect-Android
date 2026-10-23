package com.example.careconnect.domain.model

import androidx.annotation.StringRes
import com.example.careconnect.R
import com.example.careconnect.util.ResourceHelper

enum class OrganizationType(val value: String, @StringRes private val labelRes: Int) {
    PFLEGEDIENST("pflegedienst", R.string.pflegedienst),
    ARZTPRAXIS("arztpraxis", R.string.arztpraxis),
    KLINIK("klinik", R.string.klinik);

    // Text in der gewählten Sprache
    val displayName: String
        get() = ResourceHelper.getString(labelRes)

    companion object {
        fun fromValue(value: String?): OrganizationType =
            entries.firstOrNull { it.value == value } ?: PFLEGEDIENST
    }
}
