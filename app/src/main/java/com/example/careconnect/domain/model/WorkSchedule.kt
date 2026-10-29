package com.example.careconnect.domain.model

// Arbeitszeiten eines Mitarbeiters
data class WorkSchedule(
    val weeklyHours: Int = 40,
    val shiftStart: String = "08:00",
    val shiftEnd: String = "16:30",
    val workDays: List<String> = listOf("Mo", "Di", "Mi", "Do", "Fr")
)
