package com.example.careconnect.ui.patient.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.domain.model.MedicineEntry
import com.example.careconnect.domain.model.TimeOfDay
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.MedicineRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Medikamente des eingeloggten Patienten
class MedicineViewModel(
    private val medicineRepo: MedicineRepository,
    authRepo: AuthRepository
) : ViewModel() {

    private val patientId = authRepo.currentUserId

    val medicines: StateFlow<List<MedicineEntry>> =
        (if (patientId != null) medicineRepo.getMedicines(patientId) else flowOf(emptyList()))
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addMedicine(name: String, dosage: String, timeOfDay: TimeOfDay) {
        val id = patientId ?: return
        viewModelScope.launch {
            medicineRepo.addMedicine(
                id,
                MedicineEntry(name = name.trim(), dosage = dosage.trim(), timeOfDay = timeOfDay)
            )
        }
    }

    fun deleteMedicine(medicine: MedicineEntry) {
        val id = patientId ?: return
        viewModelScope.launch {
            medicineRepo.deleteMedicine(id, medicine.id)
        }
    }
}
