package com.example.careconnect.domain.repository

import com.example.careconnect.domain.model.EmergencyContact
import com.example.careconnect.domain.model.Patient
import kotlinx.coroutines.flow.Flow

// Patienten der aktuellen Organisation
interface PatientRepository {
    fun getPatients(): Flow<List<Patient>>
    fun getPatient(id: String): Flow<Patient?>
    suspend fun updateProfile(patient: Patient): Result<Unit>
    suspend fun updateProfileImage(id: String, url: String): Result<Unit>
    suspend fun saveEmergencyContact(id: String, contact: EmergencyContact): Result<Unit>
}
