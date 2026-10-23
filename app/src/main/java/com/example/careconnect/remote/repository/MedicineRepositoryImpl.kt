package com.example.careconnect.remote.repository

import com.example.careconnect.domain.model.MedicineEntry
import com.example.careconnect.domain.repository.MedicineRepository
import com.example.careconnect.remote.CollectionNames
import com.example.careconnect.remote.listenAsFlow
import com.example.careconnect.remote.model.MedicineDto
import com.example.careconnect.remote.model.toDto
import com.example.careconnect.remote.model.toMedicineEntry
import com.example.careconnect.remote.org
import com.example.careconnect.session.OrgSession
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class MedicineRepositoryImpl(
    private val db: FirebaseFirestore,
    private val session: OrgSession
) : MedicineRepository {

    private fun medicinesRef(patientId: String, orgId: String = session.requireOrgId()) = db.org(orgId)
        .collection(CollectionNames.PATIENTS)
        .document(patientId)
        .collection(CollectionNames.MEDICINES)

    override fun getMedicines(patientId: String): Flow<List<MedicineEntry>> = session.inOrg(emptyList()) { orgId ->
        medicinesRef(patientId, orgId).listenAsFlow()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    runCatching { doc.toObject<MedicineDto>()?.toMedicineEntry(doc.id) }.getOrNull()
                }
            }
            .catch { emit(emptyList()) }
    }

    override suspend fun addMedicine(patientId: String, medicine: MedicineEntry): Result<Unit> = runCatching {
        medicinesRef(patientId).add(medicine.toDto()).await()
        Unit
    }

    override suspend fun deleteMedicine(patientId: String, medicineId: String): Result<Unit> = runCatching {
        medicinesRef(patientId).document(medicineId).delete().await()
        Unit
    }
}
