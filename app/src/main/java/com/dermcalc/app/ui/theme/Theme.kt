package com.dermcalc.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

// "Tema clinico con blu primario, sfondo bianco, rosso per errori di validazione"
private val LightColorScheme = lightColorScheme(
    primary = ClinicalBlue,
    onPrimary = Color.White,
    primaryContainer = ClinicalBlueDark,
    onPrimaryContainer = Color.White,
    secondary = ClinicalBlue, // Usiamo blu anche per bottoni secondari ove applicabile
    onSecondary = Color.White,
    error = ValidationErrorRed,
    onError = Color.White,
    background = ClinicalBackground,
    onBackground = TextPrimary,
    surface = ClinicalSurface,
    onSurface = TextPrimary
)

@Composable
fun DermCalcTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}