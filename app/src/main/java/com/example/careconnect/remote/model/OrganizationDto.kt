package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.Organization
import com.example.careconnect.domain.model.OrganizationType
import com.google.firebase.Timestamp

// organizations/{id}
data class OrganizationDto(
    val name: String? = null,
    val type: String? = null,
    val address: String? = null,
    val ownerId: String? = null,
    val createdAt: Timestamp? = null
)
fun OrganizationDto.toOrganization(id: String): Organization = Organization(
    id = id,
    name = name ?: "",
    type = OrganizationType.fromValue(type),
    address = address ?: "",
    ownerId = ownerId ?: ""
)
