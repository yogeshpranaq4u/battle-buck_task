package com.example.battlebuck.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ArenaGold,
    secondary = SignalGreen,
    tertiary = AccentCoral,
    background = DeepBlack,
    surface = InkBlue,
    surfaceVariant = Slate700,
    onPrimary = Slate900,
    onSecondary = Slate900,
    onTertiary = Slate900,
    onBackground = Slate50,
    onSurface = Slate50,
    onSurfaceVariant = Slate100
)

private val LightColorScheme = lightColorScheme(
    primary = ArenaGold,
    secondary = SignalGreen,
    tertiary = AccentCoral,
    background = Slate50,
    surface = ColorWhite,
    surfaceVariant = Slate100,
    onPrimary = Slate900,
    onSecondary = Slate900,
    onTertiary = Slate50,
    onBackground = Slate900,
    onSurface = Slate900,
    onSurfaceVariant = Slate700
)

@Composable
fun BattlebuckTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
