package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.LinkedOrg
import com.example.careconnect.domain.model.Medication
import com.example.careconnect.domain.model.Patient
import com.google.firebase.Timestamp

// Feldnamen wie in der iOS-App, damit die bestehende Datenbank passt
data class PatientDto(
    val uid: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val address: String? = null,
    val city: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val allergies: String? = null,
    val medicalHistory: String? = null,
    val age: Int? = null,
    val birthDate: Timestamp? = null,
    val notes: String? = null,
    val profileImageURL: String? = null,
    val medications: List<MedicationDto>? = null,
    val history: List<String>? = null,
    val emergencyContact: EmergencyContactDto? = null,
    val createdAt: Timestamp? = null,
    val linkedOrgIds: List<String>? = null,
    val linkedOrgs: List<LinkedOrgDto>? = null
)
fun PatientDto.toPatient(id: String): Patient = Patient(
    id = id,
    firstName = firstName ?: "",
    lastName = lastName ?: "",
    address = address ?: "",
    city = city ?: "",
    phone = phone ?: "",
    email = email ?: "",
    allergies = allergies ?: "",
    medicalHistory = medicalHistory ?: "",
    age = age,
    birthDate = birthDate?.toMillis(),
    notes = notes ?: "",
    profileImageUrl = profileImageURL ?: "",
    medications = medications.orEmpty().mapIndexed { index, med ->
        Medication(
            id = med.id ?: index.toString(),
            name = med.name ?: ""
        )
    },
    history = history.orEmpty(),
    emergencyContact = emergencyContact?.toEmergencyContact(),
    linkedOrgs = linkedOrgs.orEmpty().mapNotNull { link ->
        val orgId = link.orgId ?: return@mapNotNull null
        LinkedOrg(orgId, link.orgName ?: "")
    }
)
