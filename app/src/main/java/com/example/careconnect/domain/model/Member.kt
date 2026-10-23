package com.example.careconnect.domain.model

import com.example.careconnect.R
import com.example.careconnect.util.ResourceHelper

data class Member(
    val uid: String,
    val firstName: String,
    val lastName: String,
    val role: MemberRole
) {
    val name: String
        get() = "$firstName $lastName".trim().ifEmpty { ResourceHelper.getString(R.string.unbekannt) }
}
