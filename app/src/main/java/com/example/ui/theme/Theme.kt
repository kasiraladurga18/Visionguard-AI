package com.example.ui.theme

import android.app.Activity
import android.os.Build
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

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimaryDark,
    primaryContainer = Color(0xFF0C4A6E),
    onPrimaryContainer = Color(0xFFBAE6FD),
    secondary = SecondaryAmber,
    onSecondary = OnSecondaryDark,
    secondaryContainer = Color(0xFF451A03),
    onSecondaryContainer = Color(0xFFFEF3C7),
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkBorder,
    error = HighContrastRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlueLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = Color(0xFFD97706),
    onSecondary = Color.White,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF334155),
    outline = Color(0xFF94A3B8),
    error = HighContrastRed,
    onError = Color.White
)

@Composable
fun VisionGuardTheme(
    darkTheme: Boolean = true, // Default to dark mode for accessibility and high contrast
    themeMode: Theme3DMode = Theme3DMode.CYBER_TACTILE,
    highContrastMode: Boolean = true,
    content: @Composable () -> Unit
) {
    val spec = getTheme3DSpec(themeMode, darkTheme)
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = spec.primaryAccent,
            onPrimary = Color.Black,
            primaryContainer = spec.surfaceVariant,
            onPrimaryContainer = spec.primaryAccent,
            secondary = spec.secondaryAccent,
            onSecondary = Color.Black,
            secondaryContainer = spec.surfaceVariant,
            onSecondaryContainer = spec.secondaryAccent,
            background = spec.backgroundBase,
            onBackground = spec.textPrimary,
            surface = spec.surfaceBase,
            onSurface = spec.textPrimary,
            surfaceVariant = spec.surfaceVariant,
            onSurfaceVariant = spec.textSecondary,
            outline = spec.borderStroke,
            error = HighContrastRed,
            onError = Color.White
        )
    } else {
        lightColorScheme(
            primary = spec.primaryAccent,
            onPrimary = Color.White,
            primaryContainer = spec.surfaceVariant,
            onPrimaryContainer = spec.primaryAccent,
            secondary = spec.secondaryAccent,
            onSecondary = Color.White,
            secondaryContainer = spec.surfaceVariant,
            onSecondaryContainer = spec.secondaryAccent,
            background = spec.backgroundBase,
            onBackground = spec.textPrimary,
            surface = spec.surfaceBase,
            onSurface = spec.textPrimary,
            surfaceVariant = spec.surfaceVariant,
            onSurfaceVariant = spec.textSecondary,
            outline = spec.borderStroke,
            error = HighContrastRed,
            onError = Color.White
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    VisionGuardTheme(darkTheme = darkTheme, content = content)
}
