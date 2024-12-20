package com.dar_hav_projects.messenger.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    background = Blue800,
    onBackground = Gray100,
    secondary = Blue300,
    onSecondary = Gray100,
    surface = Blue700,
    onSurface = Gray100,
    primary = Blue600,
)

private val LightColorScheme = lightColorScheme(
    background = White,
    onBackground = Blue800,
    secondary = Blue400,
    onSecondary = Gray100,
    surface = Gray100,
    onSurface = Blue800,
    primary = Gray100
)

@Composable
fun MessengerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val systemUiController = rememberSystemUiController()
    if (darkTheme) {
        systemUiController.setStatusBarColor(
            color = Blue800,
            darkIcons = false
        )
        systemUiController.setNavigationBarColor(
            color = Blue800,
            darkIcons = false
        )
    } else {
        systemUiController.setStatusBarColor(
            color = White,
            darkIcons = true
        )
        systemUiController.setNavigationBarColor(
            color = White,
            darkIcons = true
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}