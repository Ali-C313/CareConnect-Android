package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.MedicineEntry
import com.example.careconnect.domain.model.TimeOfDay

// patients/{id}/medicines
data class MedicineDto(
    val name: String? = null,
    val dosage: String? = null,
    val timeOfDay: String? = null
)

fun MedicineDto.toMedicineEntry(docId: String): MedicineEntry = MedicineEntry(
    id = docId,
    name = name ?: "",
    dosage = dosage ?: "",
    timeOfDay = TimeOfDay.fromValue(timeOfDay)
)

fun MedicineEntry.toDto(): MedicineDto = MedicineDto(
    name = name,
    dosage = dosage,
    timeOfDay = timeOfDay.value
)
