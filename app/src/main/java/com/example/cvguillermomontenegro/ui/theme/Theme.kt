package com.example.cvguillermomontenegro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF0F766E),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = Color(0xFF042F2E),
    secondary = Color(0xFF7C3AED),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = Color(0xFF2E1065),
    tertiary = Color(0xFF0F172A),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF5F7FB),
    onBackground = Color(0xFF111827),
    surface = Color(0xFFFBFCFE),
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFE5E7EB),
    onSurfaceVariant = Color(0xFF374151),
    outline = Color(0xFFD1D5DB),
    error = Color(0xFFB42318),
    errorContainer = Color(0xFFFEE4E2),
    onErrorContainer = Color(0xFF7A271A)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF5EEAD4),
    onPrimary = Color(0xFF042F2E),
    primaryContainer = Color(0xFF134E4A),
    onPrimaryContainer = Color(0xFFCCFBF1),
    secondary = Color(0xFFC4B5FD),
    onSecondary = Color(0xFF2E1065),
    secondaryContainer = Color(0xFF5B21B6),
    onSecondaryContainer = Color(0xFFEDE9FE),
    tertiary = Color(0xFFCBD5E1),
    onTertiary = Color(0xFF0F172A),
    background = Color(0xFF09111F),
    onBackground = Color(0xFFF3F4F6),
    surface = Color(0xFF0F172A),
    onSurface = Color(0xFFF3F4F6),
    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = Color(0xFFD1D5DB),
    outline = Color(0xFF334155),
    error = Color(0xFFF97066),
    errorContainer = Color(0xFF7A271A),
    onErrorContainer = Color(0xFFFEE4E2)
)

private val AppTypography = Typography()

@Composable
fun CVTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}
