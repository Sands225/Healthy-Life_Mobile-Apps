package com.example.healthylife.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary                = HealthGreen,
    onPrimary              = DeepNavy,
    primaryContainer       = HealthGreenMuted,
    onPrimaryContainer     = HealthGreenLight,
    secondary              = AccentTeal,
    onSecondary            = DeepNavy,
    secondaryContainer     = Color(0xFF0D2E28),
    onSecondaryContainer   = AccentTeal,
    tertiary               = AccentSage,
    onTertiary             = DeepNavy,
    tertiaryContainer      = Color(0xFF1A2E20),
    onTertiaryContainer    = AccentSage,
    background             = DeepNavy,
    onBackground           = TextPrimary,
    surface                = Slate,
    onSurface              = TextPrimary,
    surfaceVariant         = SlateLight,
    onSurfaceVariant       = TextSecondary,
    outline                = SlateLighter,
    error                  = CardPink,
    onError                = DeepNavy
)

private val LightColorScheme = lightColorScheme(
    primary              = HealthGreenDark,
    onPrimary            = Color.White,
    secondary            = AccentTeal,
    onSecondary          = Color.White,
    tertiary             = AccentSage,
    onTertiary           = Color.White,
    background           = Color(0xFFF0F7F3),
    onBackground         = Color(0xFF0D1A14),
    surface              = Color.White,
    onSurface            = Color(0xFF0D1A14),
    surfaceVariant       = Color(0xFFE0EFE7),
    onSurfaceVariant     = Color(0xFF2A4D38),
    outline              = Color(0xFFB0CCB9)
)

@Composable
fun HealthyLifeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}