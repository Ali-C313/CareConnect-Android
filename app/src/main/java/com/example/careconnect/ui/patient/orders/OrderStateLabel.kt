package com.example.careconnect.ui.patient.orders

import com.example.careconnect.R
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.util.ResourceHelper

// Statustext aus Sicht des Patienten ("Unterwegs" statt "Aktuell")
val OrderState.patientLabel: String
    get() = when (this) {
        OrderState.OFFEN -> ResourceHelper.getString(R.string.offen)
        OrderState.AKTUELL -> ResourceHelper.getString(R.string.unterwegs)
        OrderState.ERLEDIGT -> ResourceHelper.getString(R.string.erledigt)
    }
