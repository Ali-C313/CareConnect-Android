package com.example.careconnect.domain.model

data class Employee(
    val id: String,
    val firstName: String = "",
    val lastName: String = "",
    val role: String = "",
    val status: StaffStatus = StaffStatus.AVAILABLE,
    val email: String = "",
    val birthDate: Long? = null,
    val profileImageUrl: String = "",
    val workSchedule: WorkSchedule = WorkSchedule()
) {
    val name: String
        get() = "$firstName $lastName".trim()
}
