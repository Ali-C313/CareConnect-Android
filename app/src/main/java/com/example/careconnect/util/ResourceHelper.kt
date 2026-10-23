package com.example.careconnect.util

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import com.example.careconnect.R

// Texte aus der strings.xml für ViewModels, Repositories und Services
object ResourceHelper {

    private var appContext: Context? = null

    // Wird in App.onCreate() aufgerufen
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    /** @return Text in der gewählten App-Sprache */
    fun getString(@StringRes id: Int, vararg args: Any): String {
        val context = localizedContext() ?: return ""
        return if (args.isEmpty()) context.getString(id) else context.getString(id, *args)
    }

    // Rollen und Wochentage aus Firestore übersetzen
    fun translate(value: String): String =
        dbValues[value]?.let { getString(it) } ?: value

    private val dbValues = mapOf(
        "Leitung" to R.string.leitung,
        "Pflegekraft" to R.string.pflegekraft,
        "Mitarbeiter" to R.string.mitarbeiter,
        "Mo" to R.string.wochentag_mo,
        "Di" to R.string.wochentag_di,
        "Mi" to R.string.wochentag_mi,
        "Do" to R.string.wochentag_do,
        "Fr" to R.string.wochentag_fr,
        "Sa" to R.string.wochentag_sa,
        "So" to R.string.wochentag_so
    )

    // Sprache aus den Einstellungen, sonst die vom Handy
    private fun localizedContext(): Context? {
        val context = appContext ?: return null
        val locales = AppCompatDelegate.getApplicationLocales()
        if (locales.isEmpty) return context
        val config = Configuration(context.resources.configuration)
        config.setLocales(LocaleList.forLanguageTags(locales.toLanguageTags()))
        return context.createConfigurationContext(config)
    }
}
