package com.example.careconnect.ui.employee.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.domain.repository.EmployeeRepository
import com.example.careconnect.domain.repository.OrderRepository
import com.example.careconnect.domain.repository.PatientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class SearchViewModel(
    employeeRepo: EmployeeRepository,
    patientRepo: PatientRepository,
    orderRepo: OrderRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    // Gleiche Filter wie filteredEmployees / filteredPatients / filteredOrders in iOS
    val results = combine(
        _query,
        employeeRepo.getEmployees(),
        patientRepo.getPatients(),
        orderRepo.getOrders()
    ) { query, employees, patients, orders ->
        val search = query.trim().lowercase()
        if (search.isEmpty()) {
            SearchResults(employees, patients, orders)
        } else {
            SearchResults(
                employees = employees.filter {
                    "${it.firstName} ${it.lastName} ${it.role}".lowercase().contains(search)
                },
                patients = patients.filter {
                    "${it.firstName} ${it.lastName} ${it.address}".lowercase().contains(search)
                },
                orders = orders.filter {
                    it.patientName.lowercase().contains(search) ||
                            it.description.lowercase().contains(search) ||
                            it.notes.joinToString(" ").lowercase().contains(search)
                }
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SearchResults()
    )

    fun onQueryChange(newQuery: String) {
        _query.update { newQuery }
    }
}
