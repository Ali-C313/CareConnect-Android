package com.example.careconnect.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

// Datum und Uhrzeit formatieren und umrechnen
object DateHelper {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun currentTimeMillis(): Long = System.currentTimeMillis()

    private fun Long.toLocalDateTime(): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())

    private fun LocalDateTime.toMillis(): Long =
        atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    /** @return z.B. "11.10.2025" */
    fun Long.toDateString(): String = toLocalDateTime().format(dateFormatter)

    /** @return z.B. "14:30" */
    fun Long.toTimeString(): String = toLocalDateTime().format(timeFormatter)

    /** @return z.B. "11.10.2025, 14:30" */
    fun Long.toDateTimeString(): String = "${toDateString()}, ${toTimeString()}"

    /** Alter in vollen Jahren ab Geburtsdatum */
    fun Long.toAge(): Int {
        val birth = toLocalDateTime().toLocalDate()
        val today = LocalDate.now()
        var age = today.year - birth.year
        val hadBirthday = today.monthValue > birth.monthValue ||
                (today.monthValue == birth.monthValue && today.dayOfMonth >= birth.dayOfMonth)
        if (!hadBirthday) age--
        return age
    }

    fun Long.hour(): Int = toLocalDateTime().hour

    fun Long.minute(): Int = toLocalDateTime().minute

    /**
     * Der DatePicker arbeitet mit UTC-Mitternacht.
     * @return lokales Datum als UTC-Millisekunden für den DatePicker
     */
    fun Long.toPickerDateMillis(): Long =
        toLocalDateTime().toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    /**
     * Setzt Datum aus dem DatePicker und Uhrzeit zusammen.
     * @param pickerDateMillis Wert aus dem DatePicker (UTC)
     * @return Zeitpunkt in lokaler Zeitzone als Millisekunden
     */
    fun combineDateAndTime(pickerDateMillis: Long, hour: Int, minute: Int): Long {
        val date = Instant.ofEpochMilli(pickerDateMillis).atZone(ZoneOffset.UTC).toLocalDate()
        return LocalDateTime.of(date, LocalTime.of(hour, minute)).toMillis()
    }

    /**
     * Termine für wiederkehrende Aufträge.
     * @param weekdays 1 = Montag bis 7 = Sonntag
     * @return Zeitpunkte ab start (gleiche Uhrzeit) für die gewählten Wochentage
     */
    fun recurringDates(startMillis: Long, weekdays: Set<Int>, weeks: Int): List<Long> {
        val start = startMillis.toLocalDateTime()
        return (0 until weeks * 7)
            .map { offset -> start.toLocalDate().plusDays(offset.toLong()) }
            .filter { it.dayOfWeek.value in weekdays }
            .map { date -> LocalDateTime.of(date, start.toLocalTime()).toMillis() }
    }

    fun Long.isToday(): Boolean =
        toLocalDateTime().toLocalDate() == LocalDate.now()
}
