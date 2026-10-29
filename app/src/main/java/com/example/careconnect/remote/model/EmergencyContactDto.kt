package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.EmergencyContact

// Notfallkontakt im Patienten-Dokument
data class EmergencyContactDto(
    val name: String? = null,
    val relation: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null,
    val age: Int? = null
)

fun EmergencyContactDto.toEmergencyContact(): EmergencyContact = EmergencyContact(
    name = name ?: "",
    relation = relation ?: "",
    phone = phone ?: "",
    email = email ?: "",
    address = address ?: "",
    age = age
)

fun EmergencyContact.toDto(): EmergencyContactDto = EmergencyContactDto(
    name = name,
    relation = relation,
    phone = phone,
    email = email,
    address = address,
    age = age
)
