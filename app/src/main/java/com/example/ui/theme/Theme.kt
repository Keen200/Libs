package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HighDensityColorScheme = lightColorScheme(
    primary = HdPrimaryBlue,
    onPrimary = HdOnPrimary,
    primaryContainer = HdPillBackground,
    onPrimaryContainer = HdDarkText,
    secondary = HdPillBackground,
    onSecondary = HdDarkText,
    secondaryContainer = HdPureWhite,
    onSecondaryContainer = HdDarkText,
    background = HdSlateBackground,
    onBackground = HdDarkText,
    surface = HdPureWhite,
    onSurface = HdDarkText,
    surfaceVariant = HdSlateBackground,
    onSurfaceVariant = HdMutedGray,
    outline = HdBorderColor,
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HighDensityColorScheme,
        typography = Typography,
        content = content
    )
}
