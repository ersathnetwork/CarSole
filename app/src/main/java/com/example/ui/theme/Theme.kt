package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = CarSoleNeonPurple,
    secondary = CarSolePurpleGlow,
    tertiary = CarSoleNeonBlue,
    background = CarSoleBgDark,
    surface = CarSoleSurfaceDark,
    onPrimary = CarSoleTextPrimary,
    onSecondary = CarSoleTextPrimary,
    onBackground = CarSoleTextPrimary,
    onSurface = CarSoleTextPrimary,
    surfaceVariant = CarSoleCardDark,
    outline = CarSoleCardBorder
  )

private val LightColorScheme = DarkColorScheme

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
