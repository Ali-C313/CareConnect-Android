package com.example.careconnect.util

import com.example.careconnect.util.StringHelper.toKmString
import com.example.careconnect.util.StringHelper.toMinutesString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StringHelperTest {

    @Test
    fun initials_whenFullName_returnsTwoLetters() {
        assertEquals("AC", StringHelper.initials("Ali Chehab"))
    }

    @Test
    fun initials_whenEmpty_returnsQuestionMark() {
        assertEquals("?", StringHelper.initials("  "))
    }

    @Test
    fun isValidEmail_whenCorrect_returnsTrue() {
        assertTrue(StringHelper.isValidEmail("ali@careconnect.de"))
    }

    @Test
    fun isValidEmail_whenWithoutDomain_returnsFalse() {
        assertFalse(StringHelper.isValidEmail("ali@"))
    }

    @Test
    fun formattedPhone_removesSpacesAndBrackets() {
        assertEquals("+4903012345", StringHelper.formattedPhone("+49 (030) 123-45"))
    }

    @Test
    fun toKmString_roundsToOneDecimal() {
        assertEquals("3,4 km", 3400.0.toKmString())
    }

    @Test
    fun toMinutesString_roundsUp() {
        assertEquals("3 min", 125.0.toMinutesString())
    }
}
