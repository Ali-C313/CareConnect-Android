package com.example.careconnect.mock.repository

import com.example.careconnect.domain.model.CompletionReport
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.domain.repository.OrderRepository
import com.example.careconnect.mock.MockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class OrderRepositoryMock : OrderRepository {

    private val orders = MutableStateFlow(MockData.orders)

    override fun getOrders(): Flow<List<Order>> = orders

    override fun getOrder(id: String): Flow<Order?> =
        orders.map { list -> list.firstOrNull { it.id == id } }

    override fun getOrdersForPatient(patientId: String): Flow<List<Order>> =
        orders.map { list -> list.filter { it.patientId == patientId } }

    override fun hasActiveOrder(employeeId: String): Flow<Boolean> =
        orders.map { list -> list.any { it.employeeId == employeeId && it.state == OrderState.AKTUELL } }

    override suspend fun createOrder(order: Order): Result<Unit> = runCatching {
        orders.update { it + order.copy(id = order.id.ifEmpty { "o${it.size + 1}" }) }
    }

    override suspend fun updateOrder(
        id: String,
        patientId: String,
        patientName: String,
        description: String,
        dueDate: Long
    ): Result<Unit> = runCatching {
        update(id) { it.copy(patientId = patientId, patientName = patientName, description = description, dueDate = dueDate) }
    }

    override suspend fun acceptOrder(id: String, employeeId: String): Result<Unit> = runCatching {
        update(id) { it.copy(state = OrderState.AKTUELL, employeeId = employeeId) }
    }

    override suspend fun completeOrder(id: String, report: CompletionReport): Result<Unit> = runCatching {
        update(id) { it.copy(state = OrderState.ERLEDIGT, report = report) }
    }

    override suspend fun createOrders(orders: List<Order>): Result<Unit> = runCatching {
        orders.forEach { createOrder(it) }
    }

    override suspend fun deleteOrder(id: String): Result<Unit> = runCatching {
        orders.update { list -> list.filterNot { it.id == id } }
    }

    private fun update(id: String, change: (Order) -> Order) {
        orders.update { list -> list.map { if (it.id == id) change(it) else it } }
    }
}
