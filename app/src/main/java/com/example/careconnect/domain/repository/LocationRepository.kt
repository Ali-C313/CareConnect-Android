package com.example.careconnect.domain.repository

import com.example.careconnect.domain.model.Coordinate
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun observeEmployeeLocation(employeeId: String): Flow<Coordinate?>
    suspend fun uploadLocation(employeeId: String, coordinate: Coordinate): Result<Unit>
    // Nach dem Einsatz löschen, damit kein alter Standort angezeigt wird
    suspend fun clearLocation(employeeId: String): Result<Unit>
}
