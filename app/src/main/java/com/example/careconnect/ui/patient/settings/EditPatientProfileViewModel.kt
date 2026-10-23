package com.example.careconnect.ui.patient.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.R
import com.example.careconnect.domain.model.AddressSuggestion
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.GeoRepository
import com.example.careconnect.domain.repository.PatientRepository
import com.example.careconnect.domain.repository.StorageRepository
import com.example.careconnect.util.ResourceHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditPatientProfileViewModel(
    private val patientRepo: PatientRepository,
    private val storageRepo: StorageRepository,
    private val authRepo: AuthRepository,
    private val geoRepo: GeoRepository
) : ViewModel() {

    private val uid = authRepo.currentUserId

    private val _uiState = MutableStateFlow(EditPatientUiState())
    val uiState = _uiState.asStateFlow()

    private val _imageBytes = MutableStateFlow<ByteArray?>(null)
    val imageBytes = _imageBytes.asStateFlow()

    private var searchJob: Job? = null

    init {
        val id = uid
        if (id != null) {
            viewModelScope.launch {
                // Falls noch kein Profil existiert, mit leerem Profil starten
                val patient = patientRepo.getPatient(id).first() ?: Patient(id = id)
                _uiState.update { it.copy(patient = patient) }
            }
        }
    }

    // Kleiner Helfer, damit nicht jedes Feld eine eigene copy-Kette braucht
    private fun updatePatient(change: (Patient) -> Patient) {
        _uiState.update { state ->
            state.copy(patient = state.patient?.let(change))
        }
    }

    fun onFirstNameChange(value: String) = updatePatient { it.copy(firstName = value) }
    fun onLastNameChange(value: String) = updatePatient { it.copy(lastName = value) }
    fun onCityChange(value: String) = updatePatient { it.copy(city = value) }
    fun onEmailChange(value: String) = updatePatient { it.copy(email = value) }
    fun onPhoneChange(value: String) = updatePatient { it.copy(phone = value) }
    fun onAllergiesChange(value: String) = updatePatient { it.copy(allergies = value) }
    fun onMedicalHistoryChange(value: String) = updatePatient { it.copy(medicalHistory = value) }
    fun onCurrentPasswordChange(value: String) = _uiState.update { it.copy(currentPassword = value) }
    fun onNewPasswordChange(value: String) = _uiState.update { it.copy(newPassword = value) }
    fun onConfirmPasswordChange(value: String) = _uiState.update { it.copy(confirmPassword = value) }

    fun onImagePicked(bytes: ByteArray) {
        _imageBytes.update { bytes }
    }

    fun onAddressChange(value: String) {
        updatePatient { it.copy(address = value) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            val suggestions = geoRepo.searchAddress(value).getOrDefault(emptyList())
            _uiState.update { it.copy(addressSuggestions = suggestions) }
        }
    }

    fun onSuggestionClick(suggestion: AddressSuggestion) {
        searchJob?.cancel()
        updatePatient { it.copy(address = suggestion.formatted) }
        _uiState.update { it.copy(addressSuggestions = emptyList()) }
    }

    fun save(onSuccess: () -> Unit) {
        val id = uid ?: return
        val patient = _uiState.value.patient ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            _imageBytes.value?.let { bytes ->
                storageRepo.uploadFile("patient_images/$id.jpg", bytes, "image/jpeg")
                    .onSuccess { url -> patientRepo.updateProfileImage(id, url) }
            }

            patientRepo.updateProfile(patient.copy(id = id))
                .onSuccess { onSuccess() }
                .onFailure { e -> _uiState.update { it.copy(message = ResourceHelper.getString(R.string.fehler_beim_speichern, e.message.orEmpty())) } }

            _uiState.update { it.copy(isSaving = false) }
        }
    }

    fun changePassword() {
        val state = _uiState.value
        val error = when {
            state.currentPassword.isEmpty() -> ResourceHelper.getString(R.string.aktuelles_passwort_eingeben)
            state.newPassword.length < 6 -> ResourceHelper.getString(R.string.neues_passwort_zu_kurz)
            state.newPassword != state.confirmPassword -> ResourceHelper.getString(R.string.neuen_passwoerter_stimmen_nicht_ueberein)
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(message = error) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isChangingPassword = true) }
            authRepo.changePassword(state.currentPassword, state.newPassword)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            message = ResourceHelper.getString(R.string.passwort_erfolgreich_geaendert),
                            currentPassword = "",
                            newPassword = "",
                            confirmPassword = ""
                        )
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(message = ResourceHelper.getString(R.string.fehler_beim_aendern_des_passworts, e.message.orEmpty())) } }
            _uiState.update { it.copy(isChangingPassword = false) }
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
