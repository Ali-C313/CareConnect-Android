package com.example.careconnect.ui.employee.orderDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.careconnect.R
import com.example.careconnect.domain.model.CompletionReport
import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.Employee
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.domain.model.RouteInfo
import com.example.careconnect.domain.model.isSameRouteRequest
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.EmployeeRepository
import com.example.careconnect.domain.repository.GeoRepository
import com.example.careconnect.domain.repository.LocationRepository
import com.example.careconnect.domain.repository.OrderRepository
import com.example.careconnect.domain.repository.PatientRepository
import com.example.careconnect.ui.employee.EmployeeRoute
import com.example.careconnect.util.ResourceHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class OrderDetailViewModel(
    private val orderRepo: OrderRepository,
    private val authRepo: AuthRepository,
    patientRepo: PatientRepository,
    employeeRepo: EmployeeRepository,
    locationRepo: LocationRepository,
    geoRepo: GeoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // ID kommt über die Route
    private val orderId = MutableStateFlow<String?>(savedStateHandle.toRoute<EmployeeRoute.OrderDetail>().orderId)

    val currentUserId: String? = authRepo.currentUserId

    private val myName: StateFlow<String> = (currentUserId?.let { employeeRepo.getEmployee(it) } ?: flowOf(null))
        .map { it?.name ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // null = lädt noch, OrderLoad(null) = nicht gefunden
    private val loaded: StateFlow<OrderLoad?> = orderId
        .filterNotNull()
        .flatMapLatest { id -> orderRepo.getOrder(id).map { OrderLoad(it) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val order: StateFlow<Order?> = loaded
        .map { it?.order }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Auftrag wurde gelöscht oder existiert nicht
    val notFound: StateFlow<Boolean> = loaded
        .map { it != null && it.order == null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val patient: StateFlow<Patient?> = order
        .map { it?.patientId ?: "" }
        .distinctUntilChanged()
        .flatMapLatest { id -> if (id.isEmpty()) flowOf(null) else patientRepo.getPatient(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val assignedEmployee: StateFlow<Employee?> = order
        .map { it?.employeeId ?: "" }
        .distinctUntilChanged()
        .flatMapLatest { id -> if (id.isEmpty()) flowOf(null) else employeeRepo.getEmployee(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Patientenadresse -> Koordinate
    val patientCoordinate: StateFlow<Coordinate?> = patient
        .map { it?.fullAddress ?: "" }
        .distinctUntilChanged()
        .mapLatest { address -> geoRepo.geocode(address).getOrNull() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Live-Standort des Mitarbeiters, nur wenn der Auftrag aktuell ist
    val employeeCoordinate: StateFlow<Coordinate?> = order
        .map { current -> current?.takeIf { it.state == OrderState.AKTUELL }?.employeeId ?: "" }
        .distinctUntilChanged()
        .flatMapLatest { id -> if (id.isEmpty()) flowOf(null) else locationRepo.observeEmployeeLocation(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // ETA und Entfernung (statt MKDirections)
    val route: StateFlow<RouteInfo?> = combine(employeeCoordinate, patientCoordinate) { employee, patient ->
        employee to patient
    }
        .distinctUntilChanged(::isSameRouteRequest)
        .mapLatest { (employee, patient) ->
            if (employee == null || patient == null) null
            else geoRepo.getRoute(employee, patient).getOrNull()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun accept() {
        val id = orderId.value ?: return
        val employeeId = authRepo.currentUserId ?: return
        runAction(ResourceHelper.getString(R.string.fehler_beim_annehmen)) {
            orderRepo.acceptOrder(id, employeeId)
        }
    }

    // Abschließen mit Leistungsnachweis
    fun complete(note: String, signature: String, signedBy: String) {
        val id = orderId.value ?: return
        val report = CompletionReport(
            note = note.trim(),
            signature = signature,
            signedBy = signedBy.trim(),
            completedAt = null,
            completedBy = currentUserId ?: "",
            completedByName = myName.value
        )
        runAction(ResourceHelper.getString(R.string.fehler_beim_abschliessen)) {
            orderRepo.completeOrder(id, report)
        }
    }

    fun delete(onDeleted: () -> Unit) {
        val id = orderId.value ?: return
        runAction(ResourceHelper.getString(R.string.fehler_beim_loeschen)) {
            orderRepo.deleteOrder(id).also { result ->
                if (result.isSuccess) onDeleted()
            }
        }
    }

    fun dismissError() {
        _errorMessage.update { null }
    }

    private fun runAction(errorText: String, action: suspend () -> Result<Unit>) {
        if (_isProcessing.value) return
        viewModelScope.launch {
            _isProcessing.update { true }
            action().onFailure { e ->
                _errorMessage.update { "$errorText: ${e.message}" }
            }
            _isProcessing.update { false }
        }
    }
}
