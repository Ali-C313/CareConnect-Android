package com.example.careconnect.ui.employee.search

import com.example.careconnect.domain.model.Employee
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.Patient

// Treffer der Suche nach Gruppen
data class SearchResults(
    val employees: List<Employee> = emptyList(),
    val patients: List<Patient> = emptyList(),
    val orders: List<Order> = emptyList()
)
