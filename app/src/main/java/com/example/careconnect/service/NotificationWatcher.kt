package com.example.careconnect.service

import com.example.careconnect.R
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.domain.model.PrescriptionStatus
import com.example.careconnect.domain.model.StaffStatus
import com.example.careconnect.domain.repository.EmployeeRepository
import com.example.careconnect.domain.repository.OrderRepository
import com.example.careconnect.domain.repository.PrescriptionRepository
import com.example.careconnect.domain.repository.SettingsRepository
import com.example.careconnect.platform.Notifier
import com.example.careconnect.util.DateHelper.toDateTimeString
import com.example.careconnect.util.ResourceHelper
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Beobachtet Aufträge und Verordnungen und zeigt lokale Benachrichtigungen,
 * solange die App läuft. Der erste Stand gilt als bekannt, gemeldet wird nur, was danach passiert.
 */
class NotificationWatcher(
    private val notifier: Notifier,
    private val settingsRepo: SettingsRepository,
    private val orderRepo: OrderRepository,
    private val prescriptionRepo: PrescriptionRepository,
    private val employeeRepo: EmployeeRepository
) {

    // Läuft, bis die Coroutine abgebrochen wird (Logout, Organisationswechsel)
    suspend fun watch(uid: String, orgId: String, isEmployee: Boolean) = coroutineScope {
        val enabled = settingsRepo.notificationsEnabled.stateIn(this, SharingStarted.Eagerly, true)

        if (isEmployee) {
            // Nur wer gerade arbeitet, bekommt etwas
            val working = employeeRepo.getEmployee(uid)
                .map { it?.status == StaffStatus.AVAILABLE || it?.status == StaffStatus.BUSY }
                .stateIn(this, SharingStarted.Eagerly, false)
            val allowed = { enabled.value && working.value }

            launch { watchNewOrders(uid, allowed) }
            launch { watchPrescriptions(orgId, allowed) }
        } else {
            launch { watchMyOrders(uid, enabled) }
        }
    }

    private suspend fun watchNewOrders(uid: String, allowed: () -> Boolean) {
        var known: Set<String>? = null
        orderRepo.getOrders().collect { orders ->
            val before = known
            if (before != null && before.isNotEmpty() && orders.isEmpty()) return@collect
            known = orders.map { it.id }.toSet()
            if (before == null || !allowed()) return@collect

            val fresh = orders.filter { order ->
                order.id !in before &&
                    order.createdBy != uid &&
                    order.state == OrderState.OFFEN &&
                    (order.employeeId.isEmpty() || order.employeeId == uid)
            }
            when {
                fresh.isEmpty() -> Unit
                fresh.size == 1 -> {
                    val order = fresh.first()
                    notifier.show(
                        id = "order-${order.id}",
                        title = if (order.employeeId == uid) ResourceHelper.getString(R.string.neuer_auftrag_dich) else ResourceHelper.getString(R.string.neuer_auftrag),
                        text = listOf(order.patientName, order.description)
                            .filter { it.isNotBlank() }
                            .joinToString(": ")
                            .ifEmpty { order.displayTitle }
                    )
                }
                else -> notifier.show(
                    id = "orders-${fresh.first().id}",
                    title = ResourceHelper.getString(R.string.anzahl_neue_auftraege, fresh.size),
                    text = fresh.map { it.patientName }.filter { it.isNotBlank() }.distinct().joinToString(", ")
                )
            }
        }
    }

    private suspend fun watchPrescriptions(orgId: String, allowed: () -> Boolean) {
        var known: Set<String>? = null
        prescriptionRepo.getPrescriptions().collect { list ->
            val incoming = list.filter { it.toOrgId == orgId && it.status == PrescriptionStatus.OFFEN }
            val before = known
            known = (before ?: emptySet()) + incoming.map { it.id }
            if (before == null || !allowed()) return@collect

            incoming.filter { it.id !in before }.forEach { prescription ->
                notifier.show(
                    id = "prescription-${prescription.id}",
                    title = ResourceHelper.getString(R.string.neue_verordnung_von, prescription.fromOrgName),
                    text = ResourceHelper.getString(R.string.titel_fuer_patient, prescription.title, prescription.patientName)
                )
            }
        }
    }

    private suspend fun watchMyOrders(uid: String, enabled: StateFlow<Boolean>) {
        var known: Map<String, Order>? = null
        orderRepo.getOrdersForPatient(uid).collect { orders ->
            val before = known
            if (before != null && before.isNotEmpty() && orders.isEmpty()) return@collect
            val now = orders.associateBy { it.id }
            known = now
            if (before == null || !enabled.value) return@collect

            orders.forEach { order ->
                val old = before[order.id]
                val what = order.description.ifBlank { order.displayTitle }
                when {
                    old == null -> if (order.createdBy != uid) {
                        val date = order.dueDate?.let { ResourceHelper.getString(R.string.am_datum, it.toDateTimeString()) } ?: ""
                        notifier.show("order-${order.id}", ResourceHelper.getString(R.string.neuer_termin_geplant), "$what$date")
                    }

                    old.state != order.state -> when (order.state) {
                        OrderState.AKTUELL -> {
                            val name = employeeName(order.employeeId)
                            notifier.show(
                                "order-${order.id}-aktuell",
                                ResourceHelper.getString(R.string.auftrag_angenommen),
                                ResourceHelper.getString(R.string.mitarbeiter_kuemmert_sich, name, what)
                            )
                        }
                        OrderState.ERLEDIGT -> notifier.show(
                            "order-${order.id}-erledigt",
                            ResourceHelper.getString(R.string.auftrag_erledigt),
                            what
                        )
                        OrderState.OFFEN -> notifier.show(
                            "order-${order.id}-offen",
                            ResourceHelper.getString(R.string.auftrag_wieder_offen),
                            what
                        )
                    }

                    old.description != order.description || old.dueDate != order.dueDate -> {
                        val date = order.dueDate?.let { " – ${it.toDateTimeString()}" } ?: ""
                        notifier.show("order-${order.id}-geaendert", ResourceHelper.getString(R.string.auftrag_geaendert), "$what$date")
                    }
                }
            }

            (before.keys - now.keys).forEach { id ->
                val old = before.getValue(id)
                if (old.state != OrderState.ERLEDIGT) {
                    notifier.show("order-$id-geloescht", ResourceHelper.getString(R.string.auftrag_abgesagt), old.description.ifBlank { old.displayTitle })
                }
            }
        }
    }

    private suspend fun employeeName(id: String): String {
        if (id.isEmpty()) return ResourceHelper.getString(R.string.ein_mitarbeiter)
        val employee = withTimeoutOrNull(3_000) { employeeRepo.getEmployee(id).first() }
        return employee?.name?.ifBlank { null } ?: ResourceHelper.getString(R.string.ein_mitarbeiter)
    }
}
