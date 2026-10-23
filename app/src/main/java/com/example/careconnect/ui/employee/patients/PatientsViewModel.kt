package com.example.careconnect.ui.employee.patients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.domain.repository.PatientRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class PatientsViewModel(
    patientRepo: PatientRepository
) : ViewModel() {

    // null = lädt noch
    val patients: StateFlow<List<Patient>?> = patientRepo.getPatients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
