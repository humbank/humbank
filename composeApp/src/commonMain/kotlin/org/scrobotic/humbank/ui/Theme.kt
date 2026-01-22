package org.scrobotic.humbank.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Define your colors in a separate file or right here
// val Purple80 = Color(0xFFD0BCFF) ...

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun HumbankUITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Note: Dynamic Color and Status Bar logic are moved to platform-specific
    // wrappers if needed, or handled by the specific platform main entry point.

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Ensure Typography is also in commonMain
        content = content
    )
}