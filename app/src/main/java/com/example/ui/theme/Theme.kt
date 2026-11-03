package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme =
  darkColorScheme(
    primary = CyanNeon,
    onPrimary = DeepBlack,
    secondary = PurpleNeon,
    onSecondary = TextWhite,
    tertiary = VoiceOrange,
    onTertiary = TextWhite,
    background = DeepNavy,
    onBackground = TextWhite,
    surface = DeepNavySecondary,
    onSurface = TextWhite,
    surfaceVariant = GlassCardBg,
    onSurfaceVariant = TextGrayLight,
    outline = GlassCardBorder,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content,
  )
}
