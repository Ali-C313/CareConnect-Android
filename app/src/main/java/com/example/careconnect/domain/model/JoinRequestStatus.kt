package com.example.careconnect.domain.model

import androidx.annotation.StringRes
import com.example.careconnect.R
import com.example.careconnect.util.ResourceHelper

enum class JoinRequestStatus(val value: String, @StringRes private val labelRes: Int) {
    PENDING("pending", R.string.ausstehend),
    ACCEPTED("accepted", R.string.angenommen),
    DECLINED("declined", R.string.abgelehnt);

    // Text in der gewählten Sprache
    val displayName: String
        get() = ResourceHelper.getString(labelRes)

    companion object {
        fun fromValue(value: String?): JoinRequestStatus =
            entries.firstOrNull { it.value == value } ?: PENDING
    }
}
