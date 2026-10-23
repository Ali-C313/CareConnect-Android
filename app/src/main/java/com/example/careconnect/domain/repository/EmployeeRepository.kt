package com.example.careconnect.domain.repository

import com.example.careconnect.domain.model.Employee
import com.example.careconnect.domain.model.EmployeeDocument
import com.example.careconnect.domain.model.StaffStatus
import kotlinx.coroutines.flow.Flow

// Mitarbeiter der aktuellen Organisation
interface EmployeeRepository {
    fun getEmployees(): Flow<List<Employee>>
    fun getEmployee(id: String): Flow<Employee?>
    suspend fun updateStatus(id: String, status: StaffStatus): Result<Unit>
    suspend fun updateProfile(id: String, firstName: String, lastName: String, role: String, email: String): Result<Unit>
    suspend fun updateProfileImage(id: String, url: String): Result<Unit>
    fun getDocuments(employeeId: String): Flow<List<EmployeeDocument>>
    suspend fun addDocument(employeeId: String, fileName: String, bytes: ByteArray): Result<Unit>
    suspend fun deleteDocument(employeeId: String, documentId: String): Result<Unit>
}
