package com.example.careconnect.ui.patient.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.R
import com.example.careconnect.domain.model.AddressSuggestion
import com.example.careconnect.domain.model.Appointment
import com.example.careconnect.domain.repository.AppointmentRepository
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.GeoRepository
import com.example.careconnect.util.ResourceHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppointmentsViewModel(
    private val appointmentRepo: AppointmentRepository,
    private val geoRepo: GeoRepository,
    authRepo: AuthRepository
) : ViewModel() {

    private val patientId = authRepo.currentUserId

    val appointments: StateFlow<List<Appointment>> =
        (if (patientId != null) appointmentRepo.getAppointments(patientId) else flowOf(emptyList()))
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _form = MutableStateFlow(AppointmentFormState())
    val form = _form.asStateFlow()

    private var searchJob: Job? = null

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    /** @param appointment null = neuer Termin */
    fun startEditing(appointment: Appointment?) {
        _form.update {
            if (appointment == null) AppointmentFormState()
            else AppointmentFormState(
                editingId = appointment.id,
                what = appointment.what,
                address = appointment.address,
                date = appointment.date,
                bringItems = appointment.bringItems,
                notes = appointment.notes
            )
        }
    }

    fun onWhatChange(value: String) = _form.update { it.copy(what = value) }
    fun onDateChange(value: Long) = _form.update { it.copy(date = value) }
    fun onNotesChange(value: String) = _form.update { it.copy(notes = value) }

    fun onAddressChange(value: String) {
        _form.update { it.copy(address = value) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            val suggestions = geoRepo.searchAddress(value).getOrDefault(emptyList())
            _form.update { it.copy(addressSuggestions = suggestions) }
        }
    }

    fun onSuggestionClick(suggestion: AddressSuggestion) {
        searchJob?.cancel()
        _form.update { it.copy(address = suggestion.formatted, addressSuggestions = emptyList()) }
    }

    fun addBringItem(item: String) {
        val trimmed = item.trim()
        if (trimmed.isEmpty()) return
        _form.update { if (trimmed in it.bringItems) it else it.copy(bringItems = it.bringItems + trimmed) }
    }

    fun removeBringItem(item: String) {
        _form.update { it.copy(bringItems = it.bringItems - item) }
    }

    fun save() {
        val id = patientId ?: return
        val state = _form.value
        val appointment = Appointment(
            id = state.editingId ?: "",
            what = state.what.trim(),
            address = state.address.trim(),
            date = state.date,
            bringItems = state.bringItems,
            notes = state.notes.trim()
        )
        viewModelScope.launch {
            val result = if (state.editingId != null) {
                appointmentRepo.updateAppointment(id, appointment)
            } else {
                appointmentRepo.addAppointment(id, appointment)
            }
            result.onFailure { e -> _errorMessage.update { ResourceHelper.getString(R.string.fehler_beim_speichern, e.message.orEmpty()) } }
        }
    }

    fun delete(appointment: Appointment) {
        val id = patientId ?: return
        viewModelScope.launch {
            appointmentRepo.deleteAppointment(id, appointment.id)
                .onFailure { e -> _errorMessage.update { ResourceHelper.getString(R.string.loeschen_fehlgeschlagen, e.message.orEmpty()) } }
        }
    }

    fun dismissError() {
        _errorMessage.update { null }
    }
}
