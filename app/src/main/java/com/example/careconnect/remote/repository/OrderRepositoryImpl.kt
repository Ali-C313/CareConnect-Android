package com.example.careconnect.remote.repository

import com.example.careconnect.R
import com.example.careconnect.domain.model.CompletionReport
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.domain.repository.OrderRepository
import com.example.careconnect.remote.CollectionNames
import com.example.careconnect.remote.listenAsFlow
import com.example.careconnect.remote.model.OrderDto
import com.example.careconnect.remote.model.toDto
import com.example.careconnect.remote.model.toOrder
import com.example.careconnect.remote.model.toTimestamp
import com.example.careconnect.remote.org
import com.example.careconnect.session.OrgSession
import com.example.careconnect.util.ResourceHelper
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class OrderRepositoryImpl(
    private val db: FirebaseFirestore,
    private val session: OrgSession
) : OrderRepository {

    private fun ordersRef(orgId: String = session.requireOrgId()) =
        db.org(orgId).collection(CollectionNames.ORDERS)

    override fun getOrders(): Flow<List<Order>> = session.inOrg(emptyList()) { orgId ->
        ordersRef(orgId).listenAsFlow()
            .map { it.toOrders() }
            .catch { emit(emptyList()) }
    }

    override fun getOrder(id: String): Flow<Order?> = session.inOrg(null) { orgId ->
        ordersRef(orgId).document(id).listenAsFlow()
            .map { doc ->
                if (!doc.exists()) null
                else runCatching { doc.toObject<OrderDto>()?.toOrder(doc.id) }.getOrNull()
            }
            .catch { emit(null) }
    }

    override fun getOrdersForPatient(patientId: String): Flow<List<Order>> = session.inOrg(emptyList()) { orgId ->
        ordersRef(orgId)
            .whereEqualTo("patientId", patientId)
            .listenAsFlow()
            .map { it.toOrders() }
            .catch { emit(emptyList()) }
    }

    // true, solange der Mitarbeiter einen angenommenen Auftrag hat
    override fun hasActiveOrder(employeeId: String): Flow<Boolean> = session.inOrg(false) { orgId ->
        ordersRef(orgId)
            .whereEqualTo("employeeId", employeeId)
            .listenAsFlow()
            .map { snapshot -> snapshot.toOrders().any { it.state == OrderState.AKTUELL } }
            .catch { emit(false) }
    }

    override suspend fun createOrder(order: Order): Result<Unit> = runCatching {
        val id = order.id.ifEmpty { UUID.randomUUID().toString() }
        ordersRef().document(id).set(order.copy(id = id).toDto()).await()
    }

    override suspend fun updateOrder(
        id: String,
        patientId: String,
        patientName: String,
        description: String,
        dueDate: Long
    ): Result<Unit> = runCatching {
        ordersRef().document(id).update(
            mapOf(
                "patientId" to patientId,
                "patientName" to patientName,
                "description" to description,
                "dueDate" to dueDate.toTimestamp()
            )
        ).await()
    }

    // Transaktion, damit nicht zwei Mitarbeiter denselben Auftrag annehmen
    override suspend fun acceptOrder(id: String, employeeId: String): Result<Unit> = runCatching {
        val ref = ordersRef().document(id)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(ref)
            if (!snapshot.exists()) {
                throw FirebaseFirestoreException(
                    ResourceHelper.getString(R.string.auftrag_existiert_nicht_mehr),
                    FirebaseFirestoreException.Code.ABORTED
                )
            }
            val state = OrderState.fromValue(snapshot.getString("state"))
            if (state != OrderState.OFFEN) {
                throw FirebaseFirestoreException(
                    ResourceHelper.getString(R.string.auftrag_schon_angenommen),
                    FirebaseFirestoreException.Code.ABORTED
                )
            }
            transaction.update(
                ref,
                mapOf(
                    "state" to OrderState.AKTUELL.value,
                    "employeeId" to employeeId
                )
            )
            Unit
        }.await()
    }

    // Abschließen mit Leistungsnachweis (Notiz + Unterschrift)
    override suspend fun completeOrder(id: String, report: CompletionReport): Result<Unit> = runCatching {
        ordersRef().document(id).update(
            mapOf(
                "state" to OrderState.ERLEDIGT.value,
                "report.note" to report.note,
                "report.signature" to report.signature,
                "report.signedBy" to report.signedBy,
                "report.completedAt" to Timestamp.now(),
                "report.completedBy" to report.completedBy,
                "report.completedByName" to report.completedByName
            )
        ).await()
    }

    override suspend fun createOrders(orders: List<Order>): Result<Unit> = runCatching {
        // Firestore erlaubt max. 500 Schreibvorgänge pro Batch
        orders.chunked(400).forEach { chunk ->
            val batch = db.batch()
            chunk.forEach { order ->
                val id = order.id.ifEmpty { UUID.randomUUID().toString() }
                batch.set(ordersRef().document(id), order.copy(id = id).toDto())
            }
            batch.commit().await()
        }
    }

    override suspend fun deleteOrder(id: String): Result<Unit> = runCatching {
        ordersRef().document(id).delete().await()
    }

    // Neueste zuerst, kaputte Dokumente überspringen
    private fun QuerySnapshot.toOrders(): List<Order> = documents
        .mapNotNull { doc ->
            runCatching { doc.toObject<OrderDto>()?.toOrder(doc.id) }.getOrNull()
        }
        .sortedByDescending { it.createdAt ?: 0L }
}
