package com.example.careconnect.ui.employee

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.careconnect.R
import com.example.careconnect.domain.model.OrganizationType
import com.example.careconnect.util.ResourceHelper

enum class EmployeeTabItem(
    val route: EmployeeRoute,
    val icon: ImageVector
) {
    HOME(EmployeeRoute.Home, Icons.Default.Home),
    ORDERS(EmployeeRoute.Orders, Icons.Default.Description),
    PRESCRIPTIONS(EmployeeRoute.Prescriptions, Icons.AutoMirrored.Filled.Assignment),
    PATIENTS(EmployeeRoute.Patients, Icons.Default.Groups),
    SETTINGS(EmployeeRoute.Settings, Icons.Default.Settings);

    // Praxis und Klinik machen Hausbesuche statt Pflegeaufträge
    fun titleFor(type: OrganizationType): String = when (this) {
        HOME -> ResourceHelper.getString(R.string.home)
        ORDERS -> if (type == OrganizationType.PFLEGEDIENST) ResourceHelper.getString(R.string.auftraege) else ResourceHelper.getString(R.string.hausbesuche)
        PRESCRIPTIONS -> ResourceHelper.getString(R.string.verordnungen)
        PATIENTS -> ResourceHelper.getString(R.string.patienten)
        SETTINGS -> ResourceHelper.getString(R.string.einstellungen)
    }
}
