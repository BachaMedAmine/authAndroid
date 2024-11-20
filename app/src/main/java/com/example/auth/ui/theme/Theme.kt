package com.example.auth.ui.theme

import android.app.Activity
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

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFB4C424), // Couleur pour "OVA"
    secondary = Color(0xFF4E4E4E), // Une couleur secondaire adaptée au mode sombre
    tertiary = Color(0xFFB4C424), // Tertiaire en mode sombre si besoin
    background = Color(0xFF121212), // Fond sombre
    surface = Color(0xFF1E1E1E), // Surface sombre
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFB4C424), // Couleur pour "OVA"
    secondary = Color(0xFFA9A9A9), // Gris clair pour le texte secondaire
    tertiary = Color(0xFFB4C424), // Tertiaire pour le mode clair si besoin
    background = Color(0xFFFFFBFE), // Fond clair
    surface = Color(0xFFFFFBFE), // Surface claire
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

@Composable
fun AuthTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}