package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Professional Polish Palette
val ProfessionalBackground = Color(0xFFF8F9FF)
val ProfessionalText = Color(0xFF191C1E)
val ProfessionalSupportingText = Color(0xFF44474E)
val ProfessionalPrimary = Color(0xFF001D36)
val ProfessionalLightAccent = Color(0xFFD1E4FF)
val ProfessionalSecondaryContainer = Color(0xFFE1E2EC)
val ProfessionalBottomNavBg = Color(0xFFF0F1F9)
val ProfessionalBorder = Color(0xFFC4C6D0)

private val LightColorScheme = lightColorScheme(
    primary = ProfessionalPrimary,
    onPrimary = Color.White,
    primaryContainer = ProfessionalLightAccent,
    onPrimaryContainer = ProfessionalPrimary,
    secondary = ProfessionalSupportingText,
    onSecondary = Color.White,
    secondaryContainer = ProfessionalSecondaryContainer,
    onSecondaryContainer = ProfessionalSupportingText,
    background = ProfessionalBackground,
    onBackground = ProfessionalText,
    surface = Color.White,
    onSurface = ProfessionalText,
    outline = ProfessionalBorder
)

private val DarkColorScheme = LightColorScheme

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    // Lock to LightColorScheme to enforce the "Professional Polish" light theme
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
