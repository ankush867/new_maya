package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MayaDarkColorScheme = darkColorScheme(
    primary = MayaCyanNeon,
    onPrimary = Color(0xFF041424),
    primaryContainer = Color(0xFF003D5C),
    onPrimaryContainer = Color(0xFFB8E8FF),
    secondary = MayaBlueNeon,
    onSecondary = Color(0xFF001D3D),
    secondaryContainer = Color(0xFF1E3A8A),
    onSecondaryContainer = Color(0xFFD6E4FF),
    tertiary = MayaPurpleNeon,
    onTertiary = Color(0xFF24005A),
    tertiaryContainer = Color(0xFF4C1D95),
    onTertiaryContainer = Color(0xFFE9D5FF),
    background = MayaNavyBackground,
    onBackground = MayaTextPrimary,
    surface = MayaCardSurface,
    onSurface = MayaTextPrimary,
    surfaceVariant = MayaCardSurfaceElevated,
    onSurfaceVariant = MayaTextSecondary,
    outline = MayaCardBorder,
    outlineVariant = Color(0xFF152238)
)

@Composable
fun MayaTheme(
    darkTheme: Boolean = true, // Maya is fundamentally dark futuristic
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = MayaNavyBackground.toArgb()
                window.navigationBarColor = MayaNavyBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = MayaDarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MayaTheme(darkTheme = true, content = content)
}
