package com.example.healthylife.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider // Keep these here
import androidx.compose.runtime.compositionLocalOf      // Keep these here
import androidx.compose.ui.graphics.Color

// All imports are now at the top

private val DarkColorScheme = darkColorScheme(
    primary                = HealthGreen,
    onPrimary              = DarkDeepNavy,
    primaryContainer       = HealthGreenMuted,
    onPrimaryContainer     = HealthGreenLight,
    secondary              = AccentTeal,
    onSecondary            = DarkDeepNavy,
    secondaryContainer     = Color(0xFF0D2E28),
    onSecondaryContainer   = AccentTeal,
    tertiary               = AccentSage,
    onTertiary             = DarkDeepNavy,
    tertiaryContainer      = Color(0xFF1A2E20),
    onTertiaryContainer    = AccentSage,
    background             = DarkDeepNavy,
    onBackground           = DarkTextPrimary,
    surface                = DarkSlate,
    onSurface              = DarkTextPrimary,
    surfaceVariant         = DarkSlateLight,
    onSurfaceVariant       = DarkTextSecondary,
    outline                = DarkSlateLighter,
    error                  = CardPink,
    onError                = DarkDeepNavy
)

private val LightColorScheme = lightColorScheme(
    primary              = HealthGreenDark,
    onPrimary            = Color.White,
    secondary            = AccentTeal,
    onSecondary          = Color.White,
    tertiary             = AccentSage,
    onTertiary           = Color.White,
    background           = LightDeepNavy,
    onBackground         = LightTextPrimary,
    surface              = LightSlate,
    onSurface            = LightTextPrimary,
    surfaceVariant       = LightSlateLight,
    onSurfaceVariant     = LightTextSecondary,
    outline              = LightSlateLighter
)

// REMOVED: Duplicate imports that were here (lines 48-49)

val LocalDarkTheme = compositionLocalOf { true }
val LocalThemeToggle = compositionLocalOf<() -> Unit> { {} }

@Composable
fun HealthyLifeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    CompositionLocalProvider(
        LocalDarkTheme provides darkTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = Typography,
            content     = content
        )
    }
}