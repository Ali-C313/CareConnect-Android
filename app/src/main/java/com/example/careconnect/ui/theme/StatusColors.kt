package com.example.careconnect.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.domain.model.StaffStatus

// Farben und Icons gehören zur UI, deshalb nicht direkt im Model

val OrderState.color: Color
    get() = when (this) {
        OrderState.OFFEN -> StatusOrange
        OrderState.AKTUELL -> StatusBlue
        OrderState.ERLEDIGT -> StatusGreen
    }

val OrderState.icon: ImageVector
    get() = when (this) {
        OrderState.OFFEN -> Icons.Default.Schedule
        OrderState.AKTUELL -> Icons.Default.PlayCircle
        OrderState.ERLEDIGT -> Icons.Default.CheckCircle
    }

val StaffStatus.color: Color
    get() = when (this) {
        StaffStatus.AVAILABLE -> StatusGreen
        StaffStatus.BUSY -> StatusOrange
        StaffStatus.PAUSE -> StatusYellow
        StaffStatus.OFF -> StatusRed
        StaffStatus.VACATION -> StatusBlue
    }

val StaffStatus.icon: ImageVector
    get() = when (this) {
        StaffStatus.AVAILABLE -> Icons.Default.CheckCircle
        StaffStatus.BUSY -> Icons.Default.Bolt
        StaffStatus.PAUSE -> Icons.Default.PauseCircle
        StaffStatus.OFF -> Icons.Default.Cancel
        StaffStatus.VACATION -> Icons.Default.Flight
    }
