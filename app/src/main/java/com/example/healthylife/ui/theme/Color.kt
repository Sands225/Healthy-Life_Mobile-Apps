package com.example.healthylife.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// ── Primary – Forest Green (Monotone) ─────────────────────────────────────────
val HealthGreen      = Color(0xFF2D9E6B)   // main green
val HealthGreenDark  = Color(0xFF1E7A50)   // darker green
val HealthGreenLight = Color(0xFF4DBF87)   // lighter green
val HealthGreenMuted = Color(0xFF1A5C3E)   // very dark green for containers

// ── Dark theme defaults ───────────────────────────────────────────────────────
val DarkDeepNavy      = Color(0xFF0D1A14)
val DarkNavyDark      = Color(0xFF091210)
val DarkSlate         = Color(0xFF162619)
val DarkSlateLight    = Color(0xFF1F3828)
val DarkSlateLighter  = Color(0xFF2A4D38)
val DarkTextPrimary   = Color(0xFFE8F5EE)
val DarkTextSecondary = Color(0xFF7DAA8E)
val DarkTextMuted     = Color(0xFF4D7A60)

// ── Light theme defaults ──────────────────────────────────────────────────────
val LightDeepNavy      = Color(0xFFF0F7F3)
val LightNavyDark      = Color(0xFFE6EFEA)
val LightSlate         = Color.White
val LightSlateLight    = Color(0xFFE0EFE7)
val LightSlateLighter  = Color(0xFFB0CCB9)
val LightTextPrimary   = Color(0xFF0D1A14)
val LightTextSecondary = Color(0xFF2A4D38)
val LightTextMuted     = Color(0xFF5D8A70)

// ── Dynamic Color Getters ─────────────────────────────────────────────────────
val HeaderStart: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalDarkTheme.current) Color(0xFF0A2218) else Color(0xFFE2F3E9)

val DeepNavy: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalDarkTheme.current) DarkDeepNavy else LightDeepNavy

val NavyDark: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalDarkTheme.current) DarkNavyDark else LightNavyDark

val Slate: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalDarkTheme.current) DarkSlate else LightSlate

val SlateLight: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalDarkTheme.current) DarkSlateLight else LightSlateLight

val SlateLighter: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalDarkTheme.current) DarkSlateLighter else LightSlateLighter

val TextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalDarkTheme.current) DarkTextPrimary else LightTextPrimary

val TextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalDarkTheme.current) DarkTextSecondary else LightTextSecondary

val TextMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalDarkTheme.current) DarkTextMuted else LightTextMuted

// ── Accent Colors (kept subtle) ───────────────────────────────────────────────
val AccentTeal   = Color(0xFF29A693)   // teal
val AccentSage   = Color(0xFF6B9E7A)   // sage green
val AccentMoss   = Color(0xFF3D7A52)   // moss green

// ── Card Accents (desaturated) ────────────────────────────────────────────────
val CardOrange = Color(0xFFB87333)   // muted amber/copper
val CardPink   = Color(0xFF8A5A6E)   // muted rose
val CardYellow = Color(0xFF8A7A30)   // muted gold
val CardTeal   = Color(0xFF2A8070)   // muted teal

// ── Glass / Frosted Effect ────────────────────────────────────────────────────
val GlassWhite  = Color(0x12FFFFFF)
val GlassBorder = Color(0x1A2D9E6B)  // green-tinted glass border

// ── Legacy (kept for safety) ──────────────────────────────────────────────────
val SkyBlue     = Color(0xFF4A8FA8)   // muted blue-green
val SkyBlueDark = Color(0xFF336B80)
val SoftPurple     = Color(0xFF6B78A0)
val SoftPurpleDark = Color(0xFF4A5580)

val Purple80     = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80       = Color(0xFFEFB8C8)
val Purple40     = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40       = Color(0xFF7D5260)