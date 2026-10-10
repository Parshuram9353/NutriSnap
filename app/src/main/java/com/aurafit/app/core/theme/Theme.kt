package com.aurafit.app.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Premium HSL-aligned color system definitions for fitness indicators
val ObsidianDark = Color(0xFF0B0D14)
val SurfaceDark = Color(0xFF121621)
val SlateBorder = Color(0xFF1E2436)

val StepYellow = Color(0xFFFFB03A)
val MacroViolet = Color(0xFF8B5CF6)
val HeartRed = Color(0xFFFF4D4D)
val Spo2Magenta = Color(0xFFFF46A3)
val SleepBlue = Color(0xFF4D79FF)
val WeightTeal = Color(0xFF10B981)
val GpsGreen = Color(0xFF22C55E)
val WaterSky = Color(0xFF06B6D4)

private val DarkColorScheme = darkColorScheme(
    primary = MacroViolet,
    secondary = WaterSky,
    background = ObsidianDark,
    surface = SurfaceDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFF3F4F6),
    onSurface = Color(0xFFF3F4F6)
)

@Composable
fun AuraFitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We enforce DarkColorScheme to respect the "dark-mode optimized" requirement
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
