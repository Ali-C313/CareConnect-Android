package com.example.careconnect.ui.employee.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.R
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.OrderRepository
import com.example.careconnect.domain.repository.PatientRepository
import com.example.careconnect.util.DateHelper.currentTimeMillis
import com.example.careconnect.util.ResourceHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Neuer Auftrag oder Auftrag bearbeiten (NewOrderSheet in iOS)
class NewOrderViewModel(
    private val orderRepo: OrderRepository,
    private val authRepo: AuthRepository,
    patientRepo: PatientRepository
) : ViewModel() {

    val patients: StateFlow<List<Patient>> = patientRepo.getPatients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(NewOrderUiState())
    val uiState = _uiState.asStateFlow()

    /** @param order null = neuer Auftrag */
    fun start(order: Order?) {
        _uiState.update {
            if (order == null) NewOrderUiState()
            else NewOrderUiState(
                existingOrderId = order.id,
                selectedPatientId = order.patientId,
                description = order.description,
                dueDate = order.dueDate ?: currentTimeMillis()
            )
        }
    }

    fun onPatientSelect(patientId: String) = _uiState.update { it.copy(selectedPatientId = patientId) }
    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value) }
    fun onDueDateChange(value: Long) = _uiState.update { it.copy(dueDate = value) }

    fun save(onSuccess: () -> Unit) {
        val state = _uiState.value
        val patient = patients.value.firstOrNull { it.id == state.selectedPatientId }
        if (patient == null) {
            _uiState.update { it.copy(errorMessage = ResourceHelper.getString(R.string.fehler_patient_nicht_gefunden)) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            val result = if (state.existingOrderId != null) {
                orderRepo.updateOrder(
                    id = state.existingOrderId,
                    patientId = patient.id,
                    patientName = patient.name,
                    description = state.description.trim(),
                    dueDate = state.dueDate
                )
            } else {
                orderRepo.createOrder(
                    Order(
                        id = "",
                        state = OrderState.OFFEN,
                        description = state.description.trim(),
                        patientId = patient.id,
                        patientName = patient.name,
                        createdBy = authRepo.currentUserId ?: "unbekannt",
                        dueDate = state.dueDate,
                        createdAt = currentTimeMillis()
                    )
                )
            }

            _uiState.update { it.copy(isSaving = false) }
            result
                .onSuccess { onSuccess() }
                .onFailure { e -> _uiState.update { it.copy(errorMessage = ResourceHelper.getString(R.string.fehler_beim_speichern, e.message.orEmpty())) } }
        }
    }
}
