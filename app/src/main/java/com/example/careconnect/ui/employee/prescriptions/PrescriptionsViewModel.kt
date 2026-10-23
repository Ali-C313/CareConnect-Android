package com.example.careconnect.ui.employee.prescriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.R
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.domain.model.Prescription
import com.example.careconnect.domain.model.PrescriptionStatus
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.EmployeeRepository
import com.example.careconnect.domain.repository.OrderRepository
import com.example.careconnect.domain.repository.OrganizationRepository
import com.example.careconnect.domain.repository.PatientRepository
import com.example.careconnect.domain.repository.PrescriptionRepository
import com.example.careconnect.session.OrgSession
import com.example.careconnect.util.DateHelper.currentTimeMillis
import com.example.careconnect.util.DateHelper.recurringDates
import com.example.careconnect.util.ResourceHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class PrescriptionsViewModel(
    private val prescriptionRepo: PrescriptionRepository,
    private val orderRepo: OrderRepository,
    private val authRepo: AuthRepository,
    patientRepo: PatientRepository,
    employeeRepo: EmployeeRepository,
    orgRepo: OrganizationRepository,
    session: OrgSession
) : ViewModel() {

    private val orgId: String? = session.orgId.value
    private val uid = authRepo.currentUserId

    private val orgName: StateFlow<String> = (orgId?.let { orgRepo.observeOrganization(it) } ?: flowOf(null))
        .map { it?.name ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    private val myName: StateFlow<String> = (uid?.let { employeeRepo.getEmployee(it) } ?: flowOf(null))
        .map { it?.name ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    private val _direction = MutableStateFlow(PrescriptionDirection.INCOMING)
    val direction = _direction.asStateFlow()

    val prescriptions: StateFlow<List<Prescription>> = combine(
        prescriptionRepo.getPrescriptions(),
        _direction
    ) { list, direction ->
        when (direction) {
            PrescriptionDirection.INCOMING -> list.filter { it.toOrgId == orgId }
            PrescriptionDirection.OUTGOING -> list.filter { it.fromOrgId == orgId }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val patients: StateFlow<List<Patient>> = patientRepo.getPatients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _form = MutableStateFlow(NewPrescriptionState())
    val form = _form.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    fun onDirectionChange(value: PrescriptionDirection) = _direction.update { value }

    // Formular für neue Verordnung
    fun resetForm() = _form.update { NewPrescriptionState() }
    fun onPatientSelect(id: String) = _form.update { it.copy(patientId = id, toOrgId = "", errorMessage = null) }
    fun onTargetSelect(id: String) = _form.update { it.copy(toOrgId = id) }
    fun onTitleChange(value: String) = _form.update { it.copy(title = value) }
    fun onDetailsChange(value: String) = _form.update { it.copy(details = value) }
    fun onFrequencyChange(value: String) = _form.update { it.copy(frequency = value) }
    fun onStartDateChange(value: Long) = _form.update { it.copy(startDate = value) }

    fun create(onSuccess: () -> Unit) {
        val state = _form.value
        val from = orgId ?: return
        val patient = patients.value.firstOrNull { it.id == state.patientId }
        val target = patient?.linkedOrgs?.firstOrNull { it.orgId == state.toOrgId }
        if (patient == null || target == null) {
            _form.update { it.copy(errorMessage = ResourceHelper.getString(R.string.patient_pflegedienst_auswaehlen)) }
            return
        }

        viewModelScope.launch {
            _form.update { it.copy(isSaving = true, errorMessage = null) }
            val prescription = Prescription(
                fromOrgId = from,
                fromOrgName = orgName.value,
                toOrgId = target.orgId,
                toOrgName = target.orgName,
                patientId = patient.id,
                patientName = patient.name,
                title = state.title.trim(),
                details = state.details.trim(),
                frequency = state.frequency.trim(),
                startDate = state.startDate,
                createdBy = uid ?: "",
                createdByName = myName.value,
                createdAt = currentTimeMillis()
            )
            prescriptionRepo.createPrescription(prescription)
                .onSuccess {
                    _form.update { NewPrescriptionState() }
                    _direction.update { PrescriptionDirection.OUTGOING }
                    onSuccess()
                }
                .onFailure { e ->
                    _form.update { it.copy(isSaving = false, errorMessage = ResourceHelper.getString(R.string.senden_fehlgeschlagen, e.message.orEmpty())) }
                }
        }
    }

    /**
     * Pflegedienst übernimmt die Verordnung als Aufträge.
     * @param weekdays leer = nur ein Auftrag, sonst 1 = Mo bis 7 = So
     */
    fun accept(prescription: Prescription, startMillis: Long, weekdays: Set<Int>, weeks: Int) {
        viewModelScope.launch {
            val dates = if (weekdays.isEmpty()) listOf(startMillis)
            else recurringDates(startMillis, weekdays, weeks).take(100)
            if (dates.isEmpty()) {
                _message.update { ResourceHelper.getString(R.string.kein_passender_wochentag) }
                return@launch
            }

            val description = buildString {
                append(prescription.title)
                if (prescription.frequency.isNotBlank()) append(" (${prescription.frequency})")
                if (prescription.details.isNotBlank()) append("\n${prescription.details}")
            }
            val orders = dates.map { date ->
                Order(
                    id = UUID.randomUUID().toString(),
                    state = OrderState.OFFEN,
                    notes = listOf(ResourceHelper.getString(R.string.verordnung_von, prescription.fromOrgName)),
                    description = description,
                    patientId = prescription.patientId,
                    patientName = prescription.patientName,
                    createdBy = uid ?: "",
                    dueDate = date,
                    createdAt = currentTimeMillis()
                )
            }
            val result = orderRepo.createOrders(orders).mapCatching {
                prescriptionRepo.updateStatus(prescription, PrescriptionStatus.ANGENOMMEN, orders.first().id).getOrThrow()
            }
            result
                .onSuccess { _message.update { ResourceHelper.getString(R.string.anzahl_auftraege_angelegt, orders.size) } }
                .onFailure { e -> _message.update { ResourceHelper.getString(R.string.uebernehmen_fehlgeschlagen, e.message.orEmpty()) } }
        }
    }

    fun decline(prescription: Prescription) = changeStatus(prescription, PrescriptionStatus.ABGELEHNT)

    fun complete(prescription: Prescription) = changeStatus(prescription, PrescriptionStatus.ERLEDIGT)

    fun withdraw(prescription: Prescription) {
        viewModelScope.launch {
            prescriptionRepo.withdraw(prescription)
                .onFailure { e -> _message.update { ResourceHelper.getString(R.string.zurueckziehen_fehlgeschlagen, e.message.orEmpty()) } }
        }
    }

    fun dismissMessage() = _message.update { null }

    private fun changeStatus(prescription: Prescription, status: PrescriptionStatus) {
        viewModelScope.launch {
            prescriptionRepo.updateStatus(prescription, status)
                .onFailure { e -> _message.update { ResourceHelper.getString(R.string.status_konnte_nicht_geaendert, e.message.orEmpty()) } }
        }
    }
}
