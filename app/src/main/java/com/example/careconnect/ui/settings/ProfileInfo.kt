package com.example.careconnect.ui.settings

import com.example.careconnect.R
import com.example.careconnect.util.ResourceHelper

// Name, E-Mail und Bild für den Kopf der Einstellungen
data class ProfileInfo(
    val name: String = ResourceHelper.getString(R.string.unbekannt),
    val email: String = "—",
    val imageUrl: String = ""
)
