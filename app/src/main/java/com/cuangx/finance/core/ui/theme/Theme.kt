package com.cuangx.finance.core.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = Color(0xFFE6EEF8),
    onPrimaryContainer = LightPrimary,
    secondary = LightSecondary,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE8EEF6),
    onSecondaryContainer = LightSecondary,
    tertiary = LightCta,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFDCFCE7),
    onTertiaryContainer = Color(0xFF14532D),
    error = LightError,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceMuted,
    onSurfaceVariant = LightOnSurfaceMuted,
    outline = LightOutline,
    outlineVariant = Color(0xFFCBD5E1)
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkCta,
    onPrimary = DarkOnPrimary,
    primaryContainer = Color(0xFF14532D),
    onPrimaryContainer = Color(0xFFDCFCE7),
    secondary = DarkSecondary,
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = Color(0xFF60A5FA),
    onTertiary = Color(0xFF0F172A),
    tertiaryContainer = Color(0xFF1E3A8A),
    onTertiaryContainer = Color(0xFFDBEAFE),
    error = DarkError,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2),
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceMuted,
    onSurfaceVariant = DarkOnSurfaceMuted,
    outline = DarkOutline,
    outlineVariant = Color(0xFF475569)
)

@Composable
fun CuangXFinanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    darkModePreference: String = "system",
    content: @Composable () -> Unit
) {
    val isDark = when (darkModePreference) {
        "dark" -> true
        "light" -> false
        else -> darkTheme // "system" follows system setting
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CuangXTypography,
        content = content
    )
}
