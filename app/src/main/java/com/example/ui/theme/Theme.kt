package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AlbertsonsBlue,
    onPrimary = Color.White,
    primaryContainer = AlbertsonsLightBlue,
    onPrimaryContainer = AlbertsonsDarkBlue,
    secondary = AlbertsonsRed,
    onSecondary = Color.White,
    secondaryContainer = AlbertsonsLightRed,
    onSecondaryContainer = AlbertsonsDarkRed,
    tertiary = ForUGold,
    onTertiary = DarkCharcoal,
    tertiaryContainer = LightGold,
    background = WarmCream,
    onBackground = DarkCharcoal,
    surface = Color.White,
    onSurface = DarkCharcoal,
    surfaceVariant = SurfaceGrey,
    onSurfaceVariant = TextSecondary,
    outline = BorderLight
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF64B5F6),
    onPrimary = DarkCharcoal,
    primaryContainer = AlbertsonsDarkBlue,
    secondary = Color(0xFFE57373),
    onSecondary = DarkCharcoal,
    secondaryContainer = AlbertsonsDarkRed,
    tertiary = ForUGold,
    background = Color(0xFF0F172A),
    onBackground = Color.White,
    surface = Color(0xFF1E293B),
    onSurface = Color.White
)

@Composable
fun FreshMarketTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun AlbertsonsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    FreshMarketTheme(darkTheme = darkTheme, content = content)
}

