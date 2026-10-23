package com.example.careconnect.ui.patient.settings

import com.example.careconnect.domain.model.Membership

data class SharingState(
    val entries: List<SharingEntry> = emptyList(),
    val careServices: List<Membership> = emptyList()
)
