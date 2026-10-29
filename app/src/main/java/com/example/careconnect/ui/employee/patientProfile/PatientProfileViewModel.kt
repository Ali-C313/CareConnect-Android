package com.example.careconnect.ui.employee.patientProfile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.careconnect.R
import com.example.careconnect.domain.model.AddressSuggestion
import com.example.careconnect.domain.model.Appointment
import com.example.careconnect.domain.model.EmergencyContact
import com.example.careconnect.domain.model.MedicineEntry
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.domain.repository.AppointmentRepository
import com.example.careconnect.domain.repository.GeoRepository
import com.example.careconnect.domain.repository.MedicineRepository
import com.example.careconnect.domain.repository.PatientRepository
import com.example.careconnect.ui.employee.EmployeeRoute
import com.example.careconnect.util.DateHelper.currentTimeMillis
import com.example.careconnect.util.ResourceHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PatientProfileViewModel(
    private val patientRepo: PatientRepository,
    private val geoRepo: GeoRepository,
    medicineRepo: MedicineRepository,
    appointmentRepo: AppointmentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // ID kommt über die Route
    private val patientId = MutableStateFlow<String?>(savedStateHandle.toRoute<EmployeeRoute.PatientProfile>().patientId)

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    val patient: StateFlow<Patient?> = patientId
        .filterNotNull()
        .flatMapLatest { patientRepo.getPatient(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Medikamente, die der Patient selbst in seiner App pflegt
    val medicines: StateFlow<List<MedicineEntry>> = patientId
        .filterNotNull()
        .flatMapLatest { medicineRepo.getMedicines(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Nur kommende Termine
    val appointments: StateFlow<List<Appointment>> = patientId
        .filterNotNull()
        .flatMapLatest { appointmentRepo.getAppointments(it) }
        .map { list -> list.filter { it.date >= currentTimeMillis() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Adresse -> Koordinate für die Karte
    val location: StateFlow<AddressLocation> = patient
        .filterNotNull()
        .map { it.fullAddress }
        .distinctUntilChanged()
        .transformLatest { address ->
            if (address.isBlank()) {
                emit(AddressLocation.NotFound)
            } else {
                emit(AddressLocation.Loading)
                val coordinate = geoRepo.geocode(address).getOrNull()
                emit(if (coordinate != null) AddressLocation.Found(coordinate) else AddressLocation.NotFound)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AddressLocation.Loading)

    // Notfallkontakt-Editor
    private val _editContact = MutableStateFlow(EmergencyContact())
    val editContact = _editContact.asStateFlow()

    private val _addressSuggestions = MutableStateFlow<List<AddressSuggestion>>(emptyList())
    val addressSuggestions = _addressSuggestions.asStateFlow()

    private var searchJob: Job? = null

    fun startEditingContact() {
        _editContact.update { patient.value?.emergencyContact ?: EmergencyContact() }
        _addressSuggestions.update { emptyList() }
    }

    fun onContactChange(contact: EmergencyContact) {
        _editContact.update { contact }
    }

    fun onContactAddressChange(address: String) {
        _editContact.update { it.copy(address = address) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            _addressSuggestions.update { geoRepo.searchAddress(address).getOrDefault(emptyList()) }
        }
    }

    fun onContactSuggestionClick(suggestion: AddressSuggestion) {
        searchJob?.cancel()
        _editContact.update { it.copy(address = suggestion.formatted) }
        _addressSuggestions.update { emptyList() }
    }

    fun saveEmergencyContact(onDone: () -> Unit) {
        val id = patientId.value ?: return
        viewModelScope.launch {
            patientRepo.saveEmergencyContact(id, _editContact.value)
                .onSuccess { onDone() }
                .onFailure { e -> _errorMessage.update { ResourceHelper.getString(R.string.fehler_beim_speichern, e.message.orEmpty()) } }
        }
    }

    fun dismissError() {
        _errorMessage.update { null }
    }
}
