package com.example.careconnect.ui.employee.orderDetail

import com.example.careconnect.domain.model.Order

// Hülle, damit "lädt noch" und "nicht gefunden" unterscheidbar sind
data class OrderLoad(val order: Order?)
