package com.example.careconnect.ui.employee

import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import kotlinx.serialization.Serializable

@Serializable
sealed interface EmployeeRoute {
    @Serializable
    object Home : EmployeeRoute

    @Serializable
    object Patients : EmployeeRoute

    @Serializable
    object Orders : EmployeeRoute

    @Serializable
    object Prescriptions : EmployeeRoute

    @Serializable
    object Settings : EmployeeRoute

    @Serializable
    object Search : EmployeeRoute

    @Serializable
    object EditProfile : EmployeeRoute

    @Serializable
    object ManageOrganization : EmployeeRoute

    @Serializable
    data class PatientProfile(val patientId: String) : EmployeeRoute

    @Serializable
    data class EmployeeProfile(val employeeId: String) : EmployeeRoute

    @Serializable
    data class OrderDetail(val orderId: String) : EmployeeRoute
}
