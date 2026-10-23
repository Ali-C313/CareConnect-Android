package com.example.careconnect.ui.patient.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.domain.model.Employee
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.EmployeeRepository
import com.example.careconnect.domain.repository.OrderRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

// Übersicht der eigenen Aufträge, Details in PatientOrderDetailScreen
@OptIn(ExperimentalCoroutinesApi::class)
class PatientOrdersViewModel(
    orderRepo: OrderRepository,
    employeeRepo: EmployeeRepository,
    authRepo: AuthRepository
) : ViewModel() {

    private val uid = authRepo.currentUserId ?: ""

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val orders = orderRepo.getOrdersForPatient(uid)
        .onEach { _isLoading.value = false }

    val activeOrder: StateFlow<Order?> = orders
        .map { list -> list.firstOrNull { it.state == OrderState.AKTUELL } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Nächster Termin zuerst
    val openOrders: StateFlow<List<Order>> = orders
        .map { list -> list.filter { it.state == OrderState.OFFEN }.sortedBy { it.dueDate ?: it.createdAt ?: Long.MAX_VALUE } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Zuletzt erledigt zuerst
    val doneOrders: StateFlow<List<Order>> = orders
        .map { list ->
            list.filter { it.state == OrderState.ERLEDIGT }
                .sortedByDescending { it.report?.completedAt ?: it.dueDate ?: it.createdAt ?: 0L }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeEmployee: StateFlow<Employee?> = activeOrder
        .map { it?.employeeId ?: "" }
        .distinctUntilChanged()
        .flatMapLatest { id -> if (id.isEmpty()) flowOf(null) else employeeRepo.getEmployee(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
