package com.example.kaiju.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = KaijuOrangePrimary,
    onPrimary = KaijuOnOrange,
    primaryContainer = KaijuOrangeContainer,
    onPrimaryContainer = KaijuOnOrangeContainer,

    secondary = KaijuBlueSecondary,
    onSecondary = KaijuOnBlue,
    secondaryContainer = KaijuBlueContainer,

    tertiary = KaijuPurpleTertiary,
    tertiaryContainer = KaijuPurpleContainer,

    background = KaijuBackground,
    onBackground = KaijuOnSurface,

    surface = KaijuSurface,
    onSurface = KaijuOnSurface,
    surfaceVariant = KaijuSurfaceVariant,
    onSurfaceVariant = KaijuOnSurfaceVariant,

    outline = KaijuOutline,
    error = KaijuError,
    onError = KaijuOnError,
    errorContainer = KaijuErrorContainer,
    onErrorContainer = KaijuOnErrorContainer
)

@Composable
fun KaijuTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography,
        content = content
    )
}