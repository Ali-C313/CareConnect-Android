package com.example.careconnect.ui.employee.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.domain.model.Employee
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.domain.repository.EmployeeRepository
import com.example.careconnect.domain.repository.OrderRepository
import com.example.careconnect.service.TrackingManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class OrdersViewModel(
    orderRepo: OrderRepository,
    employeeRepo: EmployeeRepository,
    trackingManager: TrackingManager
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(OrderState.OFFEN)
    val selectedFilter = _selectedFilter.asStateFlow()

    val isTrackingActive: StateFlow<Boolean> = trackingManager.isTracking

    val employees: StateFlow<List<Employee>> = employeeRepo.getEmployees()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Liste ist im Repository schon nach createdAt sortiert
    val filteredOrders: StateFlow<List<Order>> = combine(
        orderRepo.getOrders(),
        _selectedFilter
    ) { orders, filter ->
        orders.filter { it.state == filter }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onFilterChange(state: OrderState) {
        _selectedFilter.update { state }
    }
}
