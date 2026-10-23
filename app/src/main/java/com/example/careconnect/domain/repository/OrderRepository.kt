package com.example.careconnect.domain.repository

import com.example.careconnect.domain.model.CompletionReport
import com.example.careconnect.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getOrders(): Flow<List<Order>>
    fun getOrder(id: String): Flow<Order?>
    fun getOrdersForPatient(patientId: String): Flow<List<Order>>
    fun hasActiveOrder(employeeId: String): Flow<Boolean>
    suspend fun createOrder(order: Order): Result<Unit>
    suspend fun updateOrder(
        id: String,
        patientId: String,
        patientName: String,
        description: String,
        dueDate: Long
    ): Result<Unit>
    suspend fun acceptOrder(id: String, employeeId: String): Result<Unit>
    suspend fun completeOrder(id: String, report: CompletionReport): Result<Unit>
    // Mehrere Aufträge auf einmal, z.B. aus einer wiederkehrenden Verordnung
    suspend fun createOrders(orders: List<Order>): Result<Unit>
    suspend fun deleteOrder(id: String): Result<Unit>
}
