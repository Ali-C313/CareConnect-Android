package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.WorkSchedule

data class WorkScheduleDto(
    val weeklyHours: Int? = null,
    val shiftStart: String? = null,
    val shiftEnd: String? = null,
    val workDays: List<String>? = null
)
fun WorkScheduleDto?.toWorkSchedule(): WorkSchedule {
    val default = WorkSchedule()
    if (this == null) return default
    return WorkSchedule(
        weeklyHours = weeklyHours ?: default.weeklyHours,
        shiftStart = shiftStart ?: default.shiftStart,
        shiftEnd = shiftEnd ?: default.shiftEnd,
        workDays = workDays ?: default.workDays
    )
}
