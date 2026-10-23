package com.example.careconnect.platform

import java.util.Locale

// Sprache des Handys, z.B. "de" oder "tr"
fun deviceLanguage(): String = Locale.getDefault().language
