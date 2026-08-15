package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = VibrantAccentPurple,
    onPrimary = Color(0xFF381E72),
    primaryContainer = VibrantPrimaryDark,
    onPrimaryContainer = VibrantPrimaryContainer,
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = VibrantSecondaryContainer,
    tertiary = Color(0xFFEFB8C8),
    onTertiary = Color(0xFF492532),
    tertiaryContainer = Color(0xFF633B48),
    onTertiaryContainer = VibrantTertiaryContainer,
    background = Color(0xFF141218),
    onBackground = Color(0xFFE6E0E9),
    surface = Color(0xFF141218),
    onSurface = Color(0xFFE6E0E9),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = VibrantBorder,
    outline = VibrantBorderOutline
)

private val LightColorScheme = lightColorScheme(
    primary = VibrantPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = VibrantPrimaryContainer,
    onPrimaryContainer = VibrantOnPrimaryContainer,
    secondary = Color(0xFF625B71),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = VibrantSecondaryContainer,
    onSecondaryContainer = VibrantTextDark,
    tertiary = Color(0xFF7D5260),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = VibrantTertiaryContainer,
    onTertiaryContainer = VibrantTertiaryText,
    background = VibrantBackground,
    onBackground = VibrantTextPrimary,
    surface = VibrantSurface,
    onSurface = VibrantTextPrimary,
    surfaceVariant = VibrantSurfaceVariant,
    onSurfaceVariant = VibrantTextSecondary,
    outline = VibrantBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
