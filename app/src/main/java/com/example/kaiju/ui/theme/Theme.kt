package com.example.kaiju.ui.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme


import androidx.compose.runtime.Composable



private val DarkColors = darkColorScheme(
    primary = KaijuPrimario,
    secondary = KaijuSecundario,
    primaryContainer = KaijuContainer
)

@Composable
fun ProductoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography,
        content = content
        )
}