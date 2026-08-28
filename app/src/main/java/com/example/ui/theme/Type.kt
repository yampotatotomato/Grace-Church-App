package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Apple Approved Typeface Trio:
 * 1. SF Pro (San Francisco) - Apple's modern grotesque sans-serif for UI, navigation, buttons, and system controls.
 * 2. New York - Apple's companion modern serif designed for long-form reading, scripture, devotionals, and prayer reflections.
 * 3. SF Mono - Apple's monospaced companion for verse indices, timestamps, audio metrics, and reference tags.
 */
object AppleFontFamilies {
    val SfProSans: FontFamily = FontFamily.SansSerif
    val NewYorkSerif: FontFamily = FontFamily.Serif
    val SfMono: FontFamily = FontFamily.Monospace
}

enum class FontPreset(val title: String, val subtitle: String) {
    APPLE_BALANCED(
        "Apple Trio (Balanced)",
        "SF Pro (UI) + New York (Scripture) + SF Mono (Indices)"
    ),
    APPLE_SERIF_HERITAGE(
        "New York Serif Focused",
        "Reflective, classic editorial serif with SF Mono badges"
    ),
    APPLE_MODERN_SANS(
        "SF Pro Sans Modern",
        "Crisp, minimal Cupertino contemporary layout"
    )
}

/**
 * Standard Material 3 Typography built with Apple San Francisco (SF Pro) base metrics
 */
val Typography = Typography(
    // Large Display Headings (Apple SF Pro Display / New York Serif)
    displayLarge = TextStyle(
        fontFamily = AppleFontFamilies.NewYorkSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = AppleFontFamilies.NewYorkSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.3).sp
    ),
    displaySmall = TextStyle(
        fontFamily = AppleFontFamilies.SfProSans,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.2).sp
    ),

    // Screen Titles & Section Headlines
    headlineLarge = TextStyle(
        fontFamily = AppleFontFamilies.NewYorkSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.2).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = AppleFontFamilies.SfProSans,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.15).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = AppleFontFamilies.SfProSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),

    // UI Titles & Cards
    titleLarge = TextStyle(
        fontFamily = AppleFontFamilies.SfProSans,
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        lineHeight = 25.sp,
        letterSpacing = (-0.1).sp
    ),
    titleMedium = TextStyle(
        fontFamily = AppleFontFamilies.SfProSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = AppleFontFamilies.SfProSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // Body Text (SF Pro for general UI, supplemented by New York Serif for scripture)
    bodyLarge = TextStyle(
        fontFamily = AppleFontFamilies.SfProSans,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.2).sp
    ),
    bodyMedium = TextStyle(
        fontFamily = AppleFontFamilies.SfProSans,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 21.sp,
        letterSpacing = (-0.1).sp
    ),
    bodySmall = TextStyle(
        fontFamily = AppleFontFamilies.SfProSans,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp
    ),

    // Action Labels, Buttons, Navigation (SF Pro)
    labelLarge = TextStyle(
        fontFamily = AppleFontFamilies.SfProSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = AppleFontFamilies.SfMono,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp
    ),
    labelSmall = TextStyle(
        fontFamily = AppleFontFamilies.SfMono,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.4.sp
    )
)

/**
 * Dedicated Apple Typography Helper for harmonious 3-font mixing across screens
 */
object AppleTypographyStyles {
    // 1. New York Serif: Ideal for sacred scripture text, prayers, devotional quotes
    fun scriptureText(fontSizeSp: Float = 17f) = TextStyle(
        fontFamily = AppleFontFamilies.NewYorkSerif,
        fontWeight = FontWeight.Normal,
        fontSize = fontSizeSp.sp,
        lineHeight = (fontSizeSp * 1.58f).sp,
        letterSpacing = 0.1.sp
    )

    fun devotionalProse(fontSizeSp: Float = 16f) = TextStyle(
        fontFamily = AppleFontFamilies.NewYorkSerif,
        fontWeight = FontWeight.Normal,
        fontSize = fontSizeSp.sp,
        lineHeight = (fontSizeSp * 1.52f).sp,
        letterSpacing = 0.sp
    )

    fun inspirationalQuote() = TextStyle(
        fontFamily = AppleFontFamilies.NewYorkSerif,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Italic,
        fontSize = 17.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    )

    // 2. SF Pro Sans: Ideal for clean iOS buttons, headers, and UI text
    val uiHeadline = TextStyle(
        fontFamily = AppleFontFamilies.SfProSans,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.2).sp
    )

    val uiButton = TextStyle(
        fontFamily = AppleFontFamilies.SfProSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    )

    // 3. SF Mono: Ideal for precise verse numbers, time tags, audio counters, statistics
    val verseNumber = TextStyle(
        fontFamily = AppleFontFamilies.SfMono,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 0.2.sp
    )

    val audioTimer = TextStyle(
        fontFamily = AppleFontFamilies.SfMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        letterSpacing = 0.5.sp
    )

    val referenceTag = TextStyle(
        fontFamily = AppleFontFamilies.SfMono,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        letterSpacing = 0.6.sp
    )
}
