package com.example.careconnect.i18n

// Sprachen, die man in den Einstellungen wählen kann
enum class AppLanguage(
    val code: String,
    val nativeName: String,
    val isRtl: Boolean = false
) {
    GERMAN("de", "Deutsch"),
    ENGLISH("en", "English"),
    TURKISH("tr", "Türkçe"),
    ARABIC("ar", "العربية", isRtl = true),
    SPANISH("es", "Español");

    companion object {
        // Unbekannte Sprache -> Deutsch
        fun fromCode(code: String?): AppLanguage =
            entries.firstOrNull { it.code == code?.lowercase()?.take(2) } ?: GERMAN
    }
}
