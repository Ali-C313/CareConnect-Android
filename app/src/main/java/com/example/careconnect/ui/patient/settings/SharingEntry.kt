package com.example.careconnect.ui.patient.settings

import com.example.careconnect.domain.model.Membership

// Eine Praxis/Klinik und welche Pflegedienste sie beliefern darf
data class SharingEntry(
    val source: Membership,
    val linkedOrgIds: Set<String>
)
