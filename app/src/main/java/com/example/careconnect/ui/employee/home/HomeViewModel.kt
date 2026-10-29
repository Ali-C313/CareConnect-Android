package com.example.careconnect.ui.employee.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.R
import com.example.careconnect.domain.model.Employee
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.domain.model.StaffStatus
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.EmployeeRepository
import com.example.careconnect.domain.repository.OrderRepository
import com.example.careconnect.domain.repository.PatientRepository
import com.example.careconnect.util.DateHelper.isToday
import com.example.careconnect.util.ResourceHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val employeeRepo: EmployeeRepository,
    orderRepo: OrderRepository,
    authRepo: AuthRepository,
    patientRepo: PatientRepository
) : ViewModel() {

    private val uid = authRepo.currentUserId

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    val employees: StateFlow<List<Employee>> = employeeRepo.getEmployees()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<Order>> = orderRepo.getOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Heutige Einsätze: eigene und noch freie, nach Uhrzeit
    val todayTour: StateFlow<List<TourStop>> = combine(orders, patientRepo.getPatients()) { orderList, patients ->
        val addresses = patients.associate { it.id to it.fullAddress }
        orderList
            .filter { order ->
                val due = order.dueDate ?: return@filter false
                due.isToday() && order.state != OrderState.ERLEDIGT &&
                    (order.employeeId == uid || (order.state == OrderState.OFFEN && order.employeeId.isEmpty()))
            }
            .sortedBy { it.dueDate }
            .map { TourStop(it, addresses[it.patientId].orEmpty()) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentEmployee: StateFlow<Employee?> = (if (uid != null) employeeRepo.getEmployee(uid) else flowOf(null))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateStatus(status: StaffStatus) {
        val id = uid ?: return
        viewModelScope.launch {
            employeeRepo.updateStatus(id, status)
                .onFailure { e -> _errorMessage.update { ResourceHelper.getString(R.string.fehler_beim_speichern, e.message.orEmpty()) } }
        }
    }

    fun dismissError() {
        _errorMessage.update { null }
    }
}
