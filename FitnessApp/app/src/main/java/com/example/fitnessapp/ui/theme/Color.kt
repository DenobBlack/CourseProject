package com.example.fitnessapp.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Светлая палитра
val LightColors = lightColorScheme(
    primary = Color(0xFF4CAF50),
    secondary = Color(0xFFFFC107),
    tertiary = Color(0xFFE53935),
    background = Color(0xFFF8F9FA),
    surface = Color.White,
    primaryContainer = Color(0xFF5C5C6C),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF212121)
)

// Тёмная палитра
val DarkColors = darkColorScheme(
    primary = Color(0xFF81C784),
    secondary = Color(0xFFFFD54F),
    tertiary = Color(0xFFEF5350),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    primaryContainer = Color(0xFF222222),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color(0xFFF8F9FA),
    onSurface = Color(0xFFF8F9FA)
)