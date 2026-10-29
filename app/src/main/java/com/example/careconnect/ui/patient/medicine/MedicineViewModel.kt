package com.example.careconnect.ui.patient.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.R
import com.example.careconnect.domain.model.MedicineEntry
import com.example.careconnect.domain.model.TimeOfDay
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.MedicineRepository
import com.example.careconnect.util.ResourceHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Medikamente des eingeloggten Patienten
class MedicineViewModel(
    private val medicineRepo: MedicineRepository,
    authRepo: AuthRepository
) : ViewModel() {

    private val patientId = authRepo.currentUserId

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    val medicines: StateFlow<List<MedicineEntry>> =
        (if (patientId != null) medicineRepo.getMedicines(patientId) else flowOf(emptyList()))
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addMedicine(name: String, dosage: String, timeOfDay: TimeOfDay) {
        val id = patientId ?: return
        viewModelScope.launch {
            medicineRepo.addMedicine(
                id,
                MedicineEntry(name = name.trim(), dosage = dosage.trim(), timeOfDay = timeOfDay)
            ).onFailure { e -> _errorMessage.update { ResourceHelper.getString(R.string.fehler_beim_speichern, e.message.orEmpty()) } }
        }
    }

    fun deleteMedicine(medicine: MedicineEntry) {
        val id = patientId ?: return
        viewModelScope.launch {
            medicineRepo.deleteMedicine(id, medicine.id)
                .onFailure { e -> _errorMessage.update { ResourceHelper.getString(R.string.loeschen_fehlgeschlagen, e.message.orEmpty()) } }
        }
    }

    fun dismissError() {
        _errorMessage.update { null }
    }
}
