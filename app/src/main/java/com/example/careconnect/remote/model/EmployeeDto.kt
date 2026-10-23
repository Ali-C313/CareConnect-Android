package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.Employee
import com.example.careconnect.domain.model.StaffStatus
import com.google.firebase.Timestamp

data class EmployeeDto(
    val uid: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val role: String? = null,
    val status: String? = null,
    val email: String? = null,
    val birthDate: Timestamp? = null,
    val profileImageURL: String? = null,
    val workSchedule: WorkScheduleDto? = null,
    val inviteCode: String? = null
)
fun EmployeeDto.toEmployee(id: String): Employee = Employee(
    id = id,
    firstName = firstName ?: "",
    lastName = lastName ?: "",
    role = role ?: "",
    status = StaffStatus.fromValue(status),
    email = email ?: "",
    birthDate = birthDate?.toMillis(),
    profileImageUrl = profileImageURL ?: "",
    workSchedule = workSchedule.toWorkSchedule()
)
