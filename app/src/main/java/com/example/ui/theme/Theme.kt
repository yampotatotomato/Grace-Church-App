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

private val DarkColorScheme = darkColorScheme(
    primary = ChurchGoldLight,
    onPrimary = RoyalNavy,
    primaryContainer = DeepIndigo,
    onPrimaryContainer = Color.White,
    secondary = IosBlue,
    onSecondary = Color.White,
    secondaryContainer = IosCardSecondaryDark,
    onSecondaryContainer = Color.White,
    tertiary = DevotionAccent,
    background = IosBackgroundDark,
    onBackground = IosTextPrimaryDark,
    surface = IosCardDark,
    onSurface = IosTextPrimaryDark,
    surfaceVariant = IosCardSecondaryDark,
    onSurfaceVariant = IosTextSecondaryDark,
    outline = IosBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalNavy,
    onPrimary = Color.White,
    primaryContainer = IosBlueLight,
    onPrimaryContainer = RoyalNavy,
    secondary = ChurchGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1F5F9),
    onSecondaryContainer = RoyalNavy,
    tertiary = DevotionAccent,
    background = IosBackgroundLight,
    onBackground = IosTextPrimaryLight,
    surface = IosCardLight,
    onSurface = IosTextPrimaryLight,
    surfaceVariant = IosCardSecondaryLight,
    onSurfaceVariant = IosTextSecondaryLight,
    outline = IosBorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
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

