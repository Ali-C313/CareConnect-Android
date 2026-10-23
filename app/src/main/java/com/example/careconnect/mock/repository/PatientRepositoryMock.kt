package com.example.careconnect.mock.repository

import com.example.careconnect.domain.model.EmergencyContact
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.domain.repository.PatientRepository
import com.example.careconnect.mock.MockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class PatientRepositoryMock : PatientRepository {

    private val patients = MutableStateFlow(MockData.patients)

    override fun getPatients(): Flow<List<Patient>> = patients

    override fun getPatient(id: String): Flow<Patient?> =
        patients.map { list -> list.firstOrNull { it.id == id } }

    override suspend fun updateProfile(patient: Patient): Result<Unit> = runCatching {
        update(patient.id) { patient }
    }

    override suspend fun updateProfileImage(id: String, url: String): Result<Unit> = runCatching {
        update(id) { it.copy(profileImageUrl = url) }
    }

    override suspend fun saveEmergencyContact(id: String, contact: EmergencyContact): Result<Unit> = runCatching {
        update(id) { it.copy(emergencyContact = contact) }
    }

    private fun update(id: String, change: (Patient) -> Patient) {
        patients.update { list -> list.map { if (it.id == id) change(it) else it } }
    }
}
