package com.example.careconnect.ui.settings

import com.example.careconnect.R
import com.example.careconnect.util.ResourceHelper

data class ProfileInfo(
    val name: String = ResourceHelper.getString(R.string.unbekannt),
    val email: String = "—",
    val imageUrl: String = ""
)
