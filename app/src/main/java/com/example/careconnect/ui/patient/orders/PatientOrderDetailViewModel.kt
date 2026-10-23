package com.example.careconnect.ui.patient.orders

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.Employee
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.domain.model.RouteInfo
import com.example.careconnect.domain.model.isSameRouteRequest
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.EmployeeRepository
import com.example.careconnect.domain.repository.GeoRepository
import com.example.careconnect.domain.repository.LocationRepository
import com.example.careconnect.domain.repository.OrderRepository
import com.example.careconnect.domain.repository.PatientRepository
import com.example.careconnect.domain.repository.SettingsRepository
import com.example.careconnect.ui.patient.PatientRoute
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
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Ein Auftrag des Patienten mit Status, Mitarbeiter, Live-Karte und Nachweis
@OptIn(ExperimentalCoroutinesApi::class)
class PatientOrderDetailViewModel(
    orderRepo: OrderRepository,
    employeeRepo: EmployeeRepository,
    locationRepo: LocationRepository,
    patientRepo: PatientRepository,
    geoRepo: GeoRepository,
    authRepo: AuthRepository,
    private val settingsRepo: SettingsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val uid = authRepo.currentUserId ?: ""
    // ID kommt über die Route
    private val orderId = MutableStateFlow<String?>(savedStateHandle.toRoute<PatientRoute.OrderDetail>().orderId)

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    val order: StateFlow<Order?> = orderId
        .filterNotNull()
        .flatMapLatest { id -> orderRepo.getOrder(id) }
        .onEach { _isLoading.value = false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val employeeId = order
        .map { it?.employeeId ?: "" }
        .distinctUntilChanged()

    val employee: StateFlow<Employee?> = employeeId
        .flatMapLatest { id -> if (id.isEmpty()) flowOf(null) else employeeRepo.getEmployee(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // null = noch nicht gefragt
    val showLiveLocation: StateFlow<Boolean?> = settingsRepo.showLiveLocation
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Standort nur, solange der Auftrag läuft und der Patient zugestimmt hat
    val employeeCoordinate: StateFlow<Coordinate?> = combine(order, settingsRepo.showLiveLocation) { current, show ->
        if (show == true && current?.state == OrderState.AKTUELL) current.employeeId else ""
    }
        .distinctUntilChanged()
        .flatMapLatest { id -> if (id.isEmpty()) flowOf(null) else locationRepo.observeEmployeeLocation(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Ziel = eigene Adresse aus dem Patientenprofil
    val homeCoordinate: StateFlow<Coordinate?> = (if (uid.isNotEmpty()) patientRepo.getPatient(uid) else flowOf(null))
        .map { it?.fullAddress ?: "" }
        .distinctUntilChanged()
        .mapLatest { address -> geoRepo.geocode(address).getOrNull() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val route: StateFlow<RouteInfo?> = combine(employeeCoordinate, homeCoordinate) { from, to -> from to to }
        .distinctUntilChanged(::isSameRouteRequest)
        .mapLatest { (from, to) ->
            if (from == null || to == null) null
            else geoRepo.getRoute(from, to).getOrNull()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setShowLiveLocation(show: Boolean) {
        viewModelScope.launch { settingsRepo.setShowLiveLocation(show) }
    }
}
