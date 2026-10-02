package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MomOSLightColorScheme = lightColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    primaryContainer = AccentSoftLavender,
    onPrimaryContainer = PrimaryIndigoDark,
    secondary = PrimaryIndigoDark,
    onSecondary = Color.White,
    secondaryContainer = AccentSoftLavender,
    onSecondaryContainer = PrimaryIndigoDark,
    tertiary = SuccessGreen,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    outlineVariant = Color(0xFFD6D6E0),
    error = ErrorRed,
    onError = Color.White
)

private val MomOSDarkColorScheme = darkColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    primaryContainer = AccentSoftLavenderDark,
    onPrimaryContainer = AccentSoftLavender,
    secondary = Color(0xFF7E7EE8),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF222238),
    onSecondaryContainer = AccentSoftLavender,
    tertiary = Color(0xFF4AC28B),
    onTertiary = Color.Black,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    outlineVariant = Color(0xFF383844),
    error = Color(0xFFFF6B6B),
    onError = Color.Black
)

@Composable
fun MomOSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) MomOSDarkColorScheme else MomOSLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                try {
                    val insetsController = WindowCompat.getInsetsController(window, view)
                    insetsController.isAppearanceLightStatusBars = !darkTheme
                    insetsController.isAppearanceLightNavigationBars = !darkTheme
                } catch (_: Exception) {}
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MomOSTypography,
        content = content
    )
}
