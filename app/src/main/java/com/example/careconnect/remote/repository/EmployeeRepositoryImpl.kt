package com.example.careconnect.remote.repository

import com.example.careconnect.domain.model.Employee
import com.example.careconnect.domain.model.EmployeeDocument
import com.example.careconnect.domain.model.StaffStatus
import com.example.careconnect.domain.repository.EmployeeRepository
import com.example.careconnect.domain.repository.StorageRepository
import com.example.careconnect.remote.CollectionNames
import com.example.careconnect.remote.listenAsFlow
import com.example.careconnect.remote.model.EmployeeDocumentDto
import com.example.careconnect.remote.model.EmployeeDto
import com.example.careconnect.remote.model.EmployeeProfileUpdateDto
import com.example.careconnect.remote.model.toEmployee
import com.example.careconnect.remote.model.toEmployeeDocument
import com.example.careconnect.remote.org
import com.example.careconnect.session.OrgSession
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class EmployeeRepositoryImpl(
    private val db: FirebaseFirestore,
    private val storageRepo: StorageRepository,
    private val session: OrgSession
) : EmployeeRepository {

    private fun employeesRef(orgId: String = session.requireOrgId()) =
        db.org(orgId).collection(CollectionNames.EMPLOYEES)

    override fun getEmployees(): Flow<List<Employee>> = session.inOrg(emptyList()) { orgId ->
        employeesRef(orgId).listenAsFlow()
            .map { snapshot ->
                // Kaputte Dokumente überspringen statt abzustürzen
                snapshot.documents.mapNotNull { doc ->
                    runCatching { doc.toObject<EmployeeDto>()?.toEmployee(doc.id) }.getOrNull()
                }
            }
            .catch { emit(emptyList()) }
    }

    override fun getEmployee(id: String): Flow<Employee?> = session.inOrg(null) { orgId ->
        employeesRef(orgId).document(id).listenAsFlow()
            .map { doc ->
                if (!doc.exists()) null
                else runCatching { doc.toObject<EmployeeDto>()?.toEmployee(doc.id) }.getOrNull()
            }
            .catch { emit(null) }
    }

    override suspend fun updateStatus(id: String, status: StaffStatus): Result<Unit> = runCatching {
        employeesRef().document(id).update("status", status.value).await()
        Unit
    }

    override suspend fun updateProfile(
        id: String,
        firstName: String,
        lastName: String,
        role: String,
        email: String
    ): Result<Unit> = runCatching {
        val dto = EmployeeProfileUpdateDto(firstName, lastName, role, email)
        employeesRef().document(id).set(dto, SetOptions.merge()).await()
        Unit
    }

    override suspend fun updateProfileImage(id: String, url: String): Result<Unit> = runCatching {
        employeesRef().document(id).update("profileImageURL", url).await()
        Unit
    }

    override fun getDocuments(employeeId: String): Flow<List<EmployeeDocument>> = session.inOrg(emptyList()) { orgId ->
        employeesRef(orgId).document(employeeId).collection(CollectionNames.DOCUMENTS).listenAsFlow()
            .map { snapshot ->
                snapshot.documents
                    .mapNotNull { doc ->
                        runCatching { doc.toObject<EmployeeDocumentDto>()?.toEmployeeDocument(doc.id) }.getOrNull()
                    }
                    .sortedByDescending { it.uploadedAt ?: 0L }
            }
            .catch { emit(emptyList()) }
    }

    override suspend fun addDocument(
        employeeId: String,
        fileName: String,
        bytes: ByteArray
    ): Result<Unit> = runCatching {
        val path = "employeeDocuments/$employeeId/${UUID.randomUUID()}-$fileName"
        val url = storageRepo.uploadFile(path, bytes, "application/pdf").getOrThrow()

        val dto = EmployeeDocumentDto(
            fileName = fileName,
            downloadURL = url,
            uploadedAt = Timestamp.now(),
            storagePath = path
        )
        employeesRef().document(employeeId)
            .collection(CollectionNames.DOCUMENTS)
            .add(dto)
            .await()
        Unit
    }

    override suspend fun deleteDocument(employeeId: String, documentId: String): Result<Unit> = runCatching {
        val docRef = employeesRef().document(employeeId)
            .collection(CollectionNames.DOCUMENTS)
            .document(documentId)

        // Erst die Datei im Storage löschen, dann den Eintrag
        val storagePath = runCatching { docRef.get().await().getString("storagePath") }.getOrNull()
        if (!storagePath.isNullOrEmpty()) {
            storageRepo.deleteFile(storagePath)
        }
        docRef.delete().await()
        Unit
    }
}
