package com.kanzun.perbendaharaan.core.designsystem

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Primary600,
    onPrimary = Color.White,
    primaryContainer = Primary100,
    onPrimaryContainer = Primary900,
    secondary = Secondary500,
    onSecondary = Color.White,
    secondaryContainer = Secondary100,
    onSecondaryContainer = Secondary900,
    tertiary = Accent400,
    onTertiary = Primary900,
    tertiaryContainer = Accent100,
    onTertiaryContainer = Accent700,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceSoft,
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0xFF738397),
    outlineVariant = LightBorder,
    error = ErrorLight,
    onError = Color.White,
    errorContainer = ErrorSurface,
    onErrorContainer = ErrorLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = Primary200,
    onPrimary = Primary900,
    primaryContainer = Primary800,
    onPrimaryContainer = Primary100,
    secondary = Secondary300,
    onSecondary = Secondary900,
    secondaryContainer = Secondary900,
    onSecondaryContainer = Secondary200,
    tertiary = Accent400,
    onTertiary = Primary900,
    tertiaryContainer = Accent700,
    onTertiaryContainer = Accent100,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkTextSecondary,
    outline = Color(0xFF718399),
    outlineVariant = DarkBorder,
    error = Color(0xFFFFB4AB),
    onError = DarkBackground,
    errorContainer = Color(0xFF442020),
    onErrorContainer = ErrorSurface,
)

private val KanzunShapeScheme = Shapes(
    small = KanzunShapes.SmallComponent,
    medium = KanzunShapes.Card,
    large = KanzunShapes.LargeCard,
    extraLarge = KanzunShapes.HeroCard,
)

val LocalSpacing = staticCompositionLocalOf { Spacing }
val LocalElevation = staticCompositionLocalOf { Elevation }

@Composable
fun KanzunTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalSpacing provides Spacing,
        LocalElevation provides Elevation,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = KanzunTypography,
            shapes = KanzunShapeScheme,
            content = content,
        )
    }
}
