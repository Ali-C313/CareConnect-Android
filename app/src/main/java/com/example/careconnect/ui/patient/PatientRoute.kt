package com.example.careconnect.ui.patient

import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import kotlinx.serialization.Serializable

// Routen der Patienten-App
@Serializable
sealed interface PatientRoute {
    @Serializable
    object Home : PatientRoute

    @Serializable
    object Orders : PatientRoute

    @Serializable
    object Medicine : PatientRoute

    @Serializable
    object Appointments : PatientRoute

    @Serializable
    object Settings : PatientRoute

    @Serializable
    object EditProfile : PatientRoute

    @Serializable
    data class OrderDetail(val orderId: String) : PatientRoute
}
