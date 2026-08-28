package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

fun getCustomDarkColorScheme(accentTheme: AccentTheme): ColorScheme {
    return darkColorScheme(
        primary = accentTheme.accentColor,
        onPrimary = Color.Black,
        primaryContainer = accentTheme.primaryColor,
        onPrimaryContainer = Color.White,
        secondary = accentTheme.accentColor,
        onSecondary = Color.Black,
        secondaryContainer = accentTheme.secondaryContainerDark,
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
}

fun getCustomLightColorScheme(accentTheme: AccentTheme): ColorScheme {
    return lightColorScheme(
        primary = accentTheme.primaryColor,
        onPrimary = Color.White,
        primaryContainer = accentTheme.secondaryContainerLight,
        onPrimaryContainer = accentTheme.primaryColor,
        secondary = accentTheme.accentColor,
        onSecondary = Color.White,
        secondaryContainer = accentTheme.secondaryContainerLight,
        onSecondaryContainer = accentTheme.primaryColor,
        tertiary = DevotionAccent,
        background = IosBackgroundLight,
        onBackground = IosTextPrimaryLight,
        surface = IosCardLight,
        onSurface = IosTextPrimaryLight,
        surfaceVariant = IosCardSecondaryLight,
        onSurfaceVariant = IosTextSecondaryLight,
        outline = IosBorderLight
    )
}

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    accentTheme: AccentTheme = AccentTheme.GOLD_NAVY,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> getCustomDarkColorScheme(accentTheme)
        else -> getCustomLightColorScheme(accentTheme)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
