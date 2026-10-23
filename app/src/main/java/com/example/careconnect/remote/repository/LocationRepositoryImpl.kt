package com.example.careconnect.remote.repository

import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.repository.LocationRepository
import com.example.careconnect.remote.CollectionNames
import com.example.careconnect.remote.listenAsFlow
import com.example.careconnect.remote.model.EmployeeLocationDto
import com.example.careconnect.remote.model.toCoordinate
import com.example.careconnect.remote.org
import com.example.careconnect.session.OrgSession
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class LocationRepositoryImpl(
    private val db: FirebaseFirestore,
    private val session: OrgSession
) : LocationRepository {

    private fun locationsRef(orgId: String = session.requireOrgId()) =
        db.org(orgId).collection(CollectionNames.EMPLOYEE_LOCATIONS)

    override fun observeEmployeeLocation(employeeId: String): Flow<Coordinate?> = session.inOrg(null) { orgId ->
        locationsRef(orgId).document(employeeId).listenAsFlow()
            .map { doc ->
                if (!doc.exists()) null
                else runCatching { doc.toObject<EmployeeLocationDto>()?.toCoordinate() }.getOrNull()
            }
            .catch { emit(null) }
    }

    override suspend fun uploadLocation(employeeId: String, coordinate: Coordinate): Result<Unit> = runCatching {
        val dto = EmployeeLocationDto(
            lat = coordinate.latitude,
            lon = coordinate.longitude,
            updatedAt = Timestamp.now()
        )
        locationsRef().document(employeeId).set(dto).await()
        Unit
    }

    override suspend fun clearLocation(employeeId: String): Result<Unit> = runCatching {
        locationsRef().document(employeeId).delete().await()
        Unit
    }
}
