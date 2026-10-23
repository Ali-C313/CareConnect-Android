package com.example.careconnect.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.careconnect.util.ResourceHelper

private val LightColors = lightColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    secondary = Info,
    onSecondary = Color.White,
    error = Danger,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = CardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFE8F2EF),
    onSurfaceVariant = TextSecondaryLight,
    surfaceContainer = CardLight,
    surfaceContainerLow = CardLight,
    surfaceContainerHigh = Color(0xFFF0F5F3)
)

private val DarkColors = darkColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    secondary = Info,
    onSecondary = Color.White,
    error = Danger,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = CardDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF1B2124),
    onSurfaceVariant = TextSecondaryDark,
    surfaceContainer = CardDark,
    surfaceContainerLow = CardDark,
    surfaceContainerHigh = Color(0xFF1B2124)
)

@Composable
fun CareConnectTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    // In Previews läuft App.onCreate() nicht, deshalb hier auch setzen
    ResourceHelper.init(LocalContext.current)

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
