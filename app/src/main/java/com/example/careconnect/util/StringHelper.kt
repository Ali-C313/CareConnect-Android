package com.example.careconnect.util

import kotlin.math.ceil
import kotlin.math.roundToInt

object StringHelper {

    /** "Ali Chehab" -> "AC" */
    fun initials(name: String): String {
        val result = name.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
        return result.ifEmpty { "?" }
    }

    fun isValidEmail(email: String): Boolean =
        Regex("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}").matches(email.trim())

    /** Nur Ziffern (und + für Ländervorwahl) für tel:-Links */
    fun formattedPhone(number: String): String =
        number.filter { it.isDigit() || it == '+' }

    /** 3400.0 -> "3,4 km" */
    fun Double.toKmString(): String {
        val km = (this / 100).roundToInt() / 10.0
        return "$km km".replace('.', ',')
    }

    /** Sekunden -> "12 min" */
    fun Double.toMinutesString(): String = "${ceil(this / 60).toInt()} min"
}
