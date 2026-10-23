package com.example.careconnect.domain.model

data class Organization(
    val id: String,
    val name: String,
    val type: OrganizationType,
    val address: String = "",
    val ownerId: String = ""
)
