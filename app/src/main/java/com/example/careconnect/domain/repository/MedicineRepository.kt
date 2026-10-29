package com.example.careconnect.domain.repository

import com.example.careconnect.domain.model.MedicineEntry
import kotlinx.coroutines.flow.Flow

// Medikamente eines Patienten laden und bearbeiten
interface MedicineRepository {
    fun getMedicines(patientId: String): Flow<List<MedicineEntry>>
    suspend fun addMedicine(patientId: String, medicine: MedicineEntry): Result<Unit>
    suspend fun deleteMedicine(patientId: String, medicineId: String): Result<Unit>
}
