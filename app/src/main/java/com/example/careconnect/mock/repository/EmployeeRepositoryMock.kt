package com.example.careconnect.mock.repository

import com.example.careconnect.domain.model.Employee
import com.example.careconnect.domain.model.EmployeeDocument
import com.example.careconnect.domain.model.StaffStatus
import com.example.careconnect.domain.repository.EmployeeRepository
import com.example.careconnect.mock.MockData
import com.example.careconnect.util.DateHelper.currentTimeMillis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class EmployeeRepositoryMock : EmployeeRepository {

    private val employees = MutableStateFlow(MockData.employees)
    private val documents = MutableStateFlow(MockData.documents)

    override fun getEmployees(): Flow<List<Employee>> = employees

    override fun getEmployee(id: String): Flow<Employee?> =
        employees.map { list -> list.firstOrNull { it.id == id } }

    override suspend fun updateStatus(id: String, status: StaffStatus): Result<Unit> = runCatching {
        update(id) { it.copy(status = status) }
    }

    override suspend fun updateProfile(
        id: String,
        firstName: String,
        lastName: String,
        role: String,
        email: String
    ): Result<Unit> = runCatching {
        update(id) { it.copy(firstName = firstName, lastName = lastName, role = role, email = email) }
    }

    override suspend fun updateProfileImage(id: String, url: String): Result<Unit> = runCatching {
        update(id) { it.copy(profileImageUrl = url) }
    }

    override fun getDocuments(employeeId: String): Flow<List<EmployeeDocument>> = documents

    override suspend fun addDocument(employeeId: String, fileName: String, bytes: ByteArray): Result<Unit> = runCatching {
        val document = EmployeeDocument(
            id = "d${documents.value.size + 1}",
            fileName = fileName,
            downloadUrl = "",
            uploadedAt = currentTimeMillis()
        )
        documents.update { it + document }
    }

    override suspend fun deleteDocument(employeeId: String, documentId: String): Result<Unit> = runCatching {
        documents.update { list -> list.filterNot { it.id == documentId } }
    }

    private fun update(id: String, change: (Employee) -> Employee) {
        employees.update { list -> list.map { if (it.id == id) change(it) else it } }
    }
}
