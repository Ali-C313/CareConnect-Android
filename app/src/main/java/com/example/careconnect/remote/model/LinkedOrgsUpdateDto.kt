package com.example.careconnect.remote.model

// Nur für Updates mit merge = true
data class LinkedOrgsUpdateDto(
    val linkedOrgIds: List<String>,
    val linkedOrgs: List<LinkedOrgDto>
)
