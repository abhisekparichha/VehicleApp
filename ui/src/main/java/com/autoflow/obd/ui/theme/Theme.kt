package com.autoflow.obd.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColors = darkColorScheme(
    primary = AccentGreen,
    secondary = AccentBlue,
    tertiary = AccentAmber,
    background = NightPrimary,
    surface = NightSurface
)

private val LightColors = lightColorScheme(
    primary = AccentGreen,
    secondary = AccentBlue,
    tertiary = AccentAmber
)

@Composable
fun DriveTheme(
    useDarkTheme: Boolean = !isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) DarkColors else LightColors
    val systemUi = rememberSystemUiController()
    SideEffect {
        systemUi.setSystemBarsColor(color = colorScheme.background, darkIcons = !useDarkTheme)
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = DriveTypography,
        content = content
    )
}
