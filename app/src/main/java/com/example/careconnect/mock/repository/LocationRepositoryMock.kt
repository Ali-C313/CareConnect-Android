package com.example.careconnect.mock.repository

import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.repository.LocationRepository
import com.example.careconnect.mock.MockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

// Fester Standort für die Previews
class LocationRepositoryMock : LocationRepository {

    private val location = MutableStateFlow<Coordinate?>(MockData.employeeLocation)

    override fun observeEmployeeLocation(employeeId: String): Flow<Coordinate?> = location

    override suspend fun uploadLocation(employeeId: String, coordinate: Coordinate): Result<Unit> = runCatching {
        location.update { coordinate }
    }

    override suspend fun clearLocation(employeeId: String): Result<Unit> = runCatching {
        location.update { null }
    }
}
