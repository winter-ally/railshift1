package com.railshift.passenger.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class RailshiftColors(
    val background: Color,
    val surface: Color,
    val surface2: Color,
    val border: Color,
    val borderStrong: Color,
    val text: Color,
    val textSecondary: Color,
    val accent: Color,
    val accentContainer: Color,
    val onAccent: Color,
    val success: Color,
    val successContainer: Color,
    val danger: Color
)

val LightRailshiftColors = RailshiftColors(
    background = LightBackground,
    surface = LightSurface,
    surface2 = LightSurface2,
    border = LightBorder,
    borderStrong = LightBorderStrong,
    text = LightText,
    textSecondary = LightTextSecondary,
    accent = LightAccent,
    accentContainer = LightAccentContainer,
    onAccent = LightOnAccent,
    success = LightSuccess,
    successContainer = LightSuccessContainer,
    danger = LightDanger
)

val DarkRailshiftColors = RailshiftColors(
    background = DarkBackground,
    surface = DarkSurface,
    surface2 = DarkSurface2,
    border = DarkBorder,
    borderStrong = DarkBorderStrong,
    text = DarkText,
    textSecondary = DarkTextSecondary,
    accent = DarkAccent,
    accentContainer = DarkAccentContainer,
    onAccent = DarkOnAccent,
    success = DarkSuccess,
    successContainer = DarkSuccessContainer,
    danger = DarkDanger
)

val LocalRailshiftColors = staticCompositionLocalOf { LightRailshiftColors }
val LocalThemeIsDark = staticCompositionLocalOf { false }
val LocalToggleTheme = staticCompositionLocalOf<() -> Unit> { {} }

private val LightM3ColorScheme = lightColorScheme(
    primary = LightAccent,
    onPrimary = LightOnAccent,
    primaryContainer = LightAccentContainer,
    onPrimaryContainer = LightAccent,
    secondary = LightAccent,
    onSecondary = LightOnAccent,
    background = LightBackground,
    onBackground = LightText,
    surface = LightSurface,
    onSurface = LightText,
    surfaceVariant = LightSurface2,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorderStrong,
    outlineVariant = LightBorder,
    error = LightDanger,
    onError = Color.White
)

private val DarkM3ColorScheme = darkColorScheme(
    primary = DarkAccent,
    onPrimary = DarkOnAccent,
    primaryContainer = DarkAccentContainer,
    onPrimaryContainer = DarkAccent,
    secondary = DarkAccent,
    onSecondary = DarkOnAccent,
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText,
    surfaceVariant = DarkSurface2,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorderStrong,
    outlineVariant = DarkBorder,
    error = DarkDanger,
    onError = Color.Black
)

object RailshiftTheme {
    val colors: RailshiftColors
        @Composable
        get() = LocalRailshiftColors.current

    val isDark: Boolean
        @Composable
        get() = LocalThemeIsDark.current

    val toggleTheme: () -> Unit
        @Composable
        get() = LocalToggleTheme.current
}

@Composable
fun RailshiftTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    onToggleTheme: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val railshiftColors = if (darkTheme) DarkRailshiftColors else LightRailshiftColors
    val m3ColorScheme = if (darkTheme) DarkM3ColorScheme else LightM3ColorScheme

    CompositionLocalProvider(
        LocalRailshiftColors provides railshiftColors,
        LocalThemeIsDark provides darkTheme,
        LocalToggleTheme provides (onToggleTheme ?: {})
    ) {
        MaterialTheme(
            colorScheme = m3ColorScheme,
            typography = RailshiftTypography,
            shapes = RailshiftShapes,
            content = content
        )
    }
}
