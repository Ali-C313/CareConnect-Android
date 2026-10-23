package com.example.careconnect.domain.model

import com.example.careconnect.R
import com.example.careconnect.util.ResourceHelper

data class Patient(
    val id: String,
    val firstName: String = "",
    val lastName: String = "",
    val address: String = "",
    val city: String = "",
    val phone: String = "",
    val email: String = "",
    val allergies: String = "",
    val medicalHistory: String = "",
    val age: Int? = null,
    val birthDate: Long? = null,
    val notes: String = "",
    val profileImageUrl: String = "",
    val medications: List<Medication> = emptyList(),
    val history: List<String> = emptyList(),
    val emergencyContact: EmergencyContact? = null,
    val linkedOrgs: List<LinkedOrg> = emptyList()
) {
    // Vor- und Nachname, sonst E-Mail
    val name: String
        get() {
            val full = listOf(firstName.trim(), lastName.trim())
                .filter { it.isNotEmpty() }
                .joinToString(" ")
            return full.ifEmpty { email.ifEmpty { ResourceHelper.getString(R.string.unbekannt) } }
        }

    // Adresse + Stadt für Karte und Maps
    val fullAddress: String
        get() = listOf(address, city)
            .filter { it.isNotBlank() }
            .joinToString(", ")
}
