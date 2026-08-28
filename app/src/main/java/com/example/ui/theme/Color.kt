package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Default Brand Colors
val RoyalNavy = Color(0xFF0F172A)
val DeepIndigo = Color(0xFF1E293B)
val ChurchGold = Color(0xFFC29B38)
val ChurchGoldLight = Color(0xFFE6CA65)
val ChurchGoldDark = Color(0xFF99751F)
val IosBlue = Color(0xFF007AFF)
val IosBlueLight = Color(0xFFE5F1FF)

// iOS Neutral Shades
val IosBackgroundLight = Color(0xFFF2F2F7)
val IosCardLight = Color(0xFFFFFFFF)
val IosCardSecondaryLight = Color(0xFFF8FAFC)
val IosBorderLight = Color(0xFFE5E5EA)
val IosTextPrimaryLight = Color(0xFF000000)
val IosTextSecondaryLight = Color(0xFF6C6C70)
val IosTextTertiaryLight = Color(0xFF8E8E93)

// Dark Theme Shades
val IosBackgroundDark = Color(0xFF0A0E17)
val IosCardDark = Color(0xFF161E2E)
val IosCardSecondaryDark = Color(0xFF1E293B)
val IosBorderDark = Color(0xFF2E3D52)
val IosTextPrimaryDark = Color(0xFFF8FAFC)
val IosTextSecondaryDark = Color(0xFF94A3B8)
val IosTextTertiaryDark = Color(0xFF64748B)

// Accent Status Colors
val DevotionAccent = Color(0xFF7C3AED)
val ScriptureAccent = Color(0xFF2563EB)
val PrayerAccent = Color(0xFF059669)
val PastorAccent = Color(0xFFD97706)

// Theme Presets for Customization
enum class ThemeMode(val title: String, val subtitle: String) {
    SYSTEM("System Auto", "Follows device light/dark schedule"),
    LIGHT("Sanctuary Light", "Clean, high-contrast bright canvas"),
    DARK("Midnight Cathedral", "Eye-comfort dark contemplative mode")
}

enum class AccentTheme(
    val title: String,
    val description: String,
    val primaryColor: Color,
    val accentColor: Color,
    val secondaryContainerLight: Color,
    val secondaryContainerDark: Color
) {
    GOLD_NAVY(
        title = "Navy & Sanctuary Gold",
        description = "Majestic Cathedral aesthetic with warm amber gold highlights",
        primaryColor = Color(0xFF0F172A),
        accentColor = Color(0xFFC29B38),
        secondaryContainerLight = Color(0xFFFEF9C3),
        secondaryContainerDark = Color(0xFF422006)
    ),
    CUPERTINO_BLUE(
        title = "Cupertino Royal Blue",
        description = "Apple iOS signature vibrant sapphire blue with crisp azure tones",
        primaryColor = Color(0xFF0A4DA2),
        accentColor = Color(0xFF007AFF),
        secondaryContainerLight = Color(0xFFE0F2FE),
        secondaryContainerDark = Color(0xFF0C4A6E)
    ),
    EMERALD_LIVING(
        title = "Cathedral Emerald",
        description = "Symbol of living hope, spiritual renewal, and tranquility",
        primaryColor = Color(0xFF064E3B),
        accentColor = Color(0xFF10B981),
        secondaryContainerLight = Color(0xFFD1FAE5),
        secondaryContainerDark = Color(0xFF064E3B)
    ),
    ROYAL_AMETHYST(
        title = "Grace Amethyst",
        description = "Rich velvet royal purple representing majesty and divine grace",
        primaryColor = Color(0xFF3B0764),
        accentColor = Color(0xFF8B5CF6),
        secondaryContainerLight = Color(0xFFEDE9FE),
        secondaryContainerDark = Color(0xFF3B0764)
    ),
    SUNRISE_AMBER(
        title = "Dawn Sunrise Amber",
        description = "Warm golden morning glow inspiring joy and devotion",
        primaryColor = Color(0xFF78350F),
        accentColor = Color(0xFFF59E0B),
        secondaryContainerLight = Color(0xFFFEF3C7),
        secondaryContainerDark = Color(0xFF451A03)
    ),
    ROSE_SERENITY(
        title = "Rose of Sharon",
        description = "Elegant crimson and rose tones embodying unconditional love",
        primaryColor = Color(0xFF881337),
        accentColor = Color(0xFFE11D48),
        secondaryContainerLight = Color(0xFFFFE4E6),
        secondaryContainerDark = Color(0xFF4C0519)
    )
}
