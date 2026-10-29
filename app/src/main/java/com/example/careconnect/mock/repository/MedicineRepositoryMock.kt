package com.example.careconnect.mock.repository

import com.example.careconnect.domain.model.MedicineEntry
import com.example.careconnect.domain.repository.MedicineRepository
import com.example.careconnect.mock.MockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

// Beispielmedikamente für die Previews
class MedicineRepositoryMock : MedicineRepository {

    private val medicines = MutableStateFlow(MockData.medicines)

    override fun getMedicines(patientId: String): Flow<List<MedicineEntry>> = medicines

    override suspend fun addMedicine(patientId: String, medicine: MedicineEntry): Result<Unit> = runCatching {
        medicines.update { it + medicine.copy(id = "md${it.size + 1}") }
    }

    override suspend fun deleteMedicine(patientId: String, medicineId: String): Result<Unit> = runCatching {
        medicines.update { list -> list.filterNot { it.id == medicineId } }
    }
}
