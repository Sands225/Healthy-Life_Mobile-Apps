package com.example.healthylife.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary                = HealthGreen,
    onPrimary              = DeepNavy,
    primaryContainer       = Color(0xFF003D2E),
    onPrimaryContainer     = HealthGreenLight,
    secondary              = SkyBlue,
    onSecondary            = DeepNavy,
    secondaryContainer     = Color(0xFF0C2340),
    onSecondaryContainer   = Color(0xFFBAE6FD),
    tertiary               = SoftPurple,
    onTertiary             = DeepNavy,
    tertiaryContainer      = Color(0xFF2D1B69),
    onTertiaryContainer    = Color(0xFFDDD6FE),
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
    secondary            = SkyBlueDark,
    onSecondary          = Color.White,
    tertiary             = SoftPurpleDark,
    onTertiary           = Color.White,
    background           = Color(0xFFF8FAFC),
    onBackground         = Color(0xFF0F172A),
    surface              = Color.White,
    onSurface            = Color(0xFF0F172A),
    surfaceVariant       = Color(0xFFF1F5F9),
    onSurfaceVariant     = Color(0xFF475569),
    outline              = Color(0xFFCBD5E1)
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