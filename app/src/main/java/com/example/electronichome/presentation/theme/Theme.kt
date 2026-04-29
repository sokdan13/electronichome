package com.example.electronichome.presentation.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val OrangePrimary    = Color(0xFFF57C00)
val OrangeOnPrimary  = Color(0xFFFFFFFF)
val OrangeContainer  = Color(0xFFFFE0B2)

val BluePrimary      = Color(0xFF0277BD)
val BlueOnPrimary    = Color(0xFFD5E0EC)
val BlueContainer    = Color(0xFFB3E5FC)
val BlueSurface      = Color(0xFFD5E0EC)

private val LightColorScheme = lightColorScheme(
    primary          = OrangePrimary,
    onPrimary        = OrangeOnPrimary,
    primaryContainer = OrangeContainer,
    secondary        = BluePrimary,
    onSecondary      = BlueOnPrimary,
    secondaryContainer = BlueContainer,
    background       = Color(0xFFFAFAFA),
    surface          = Color(0xFFFFFFFF),
)

@Composable
fun ElectronichomeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(),
        content = content
    )
}