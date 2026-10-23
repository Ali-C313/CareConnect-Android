package com.example.careconnect.session

import com.example.careconnect.R
import com.example.careconnect.util.ResourceHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update

/**
 * Merkt sich, in welcher Organisation der Nutzer gerade arbeitet.
 * Alle Daten (Patienten, Aufträge, ...) liegen unter organizations/{orgId}.
 */
class OrgSession {

    private val _orgId = MutableStateFlow<String?>(null)
    val orgId: StateFlow<String?> = _orgId.asStateFlow()

    fun set(orgId: String?) {
        _orgId.update { orgId }
    }

    fun requireOrgId(): String =
        _orgId.value ?: throw IllegalStateException(ResourceHelper.getString(R.string.keine_organisation_ausgewaehlt))

    // Ohne Organisation kommt nur der Leerwert
    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> inOrg(empty: T, block: (String) -> Flow<T>): Flow<T> =
        orgId.flatMapLatest { id -> if (id == null) flowOf(empty) else block(id) }
}
