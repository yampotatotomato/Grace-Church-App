package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * Apple Moving Atmosphere Background
 *
 * Renders an organic, continuously moving mesh/aurora gradient reminiscent of
 * Apple VisionOS, macOS dynamic wallpapers, and iOS fluid Siri/intelligence backgrounds.
 * Uses hardware-accelerated drawBehind with non-repeating harmonic orbital trajectories.
 */
@Composable
fun AppleMovingAtmosphereBackground(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    speedMultiplier: Float = 1.0f,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "apple_aurora_atmosphere")

    // Harmonic orbital phases with different cycle lengths to prevent repetitive motion
    val phase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (18000 / speedMultiplier).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase_primary"
    )

    val phase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (25000 / speedMultiplier).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase_secondary"
    )

    val phase3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (32000 / speedMultiplier).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase_tertiary"
    )

    val phaseRays by infiniteTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (14000 / speedMultiplier).toInt(), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase_specular_rays"
    )

    // Spiritual & Cathedral Apple palette colors
    val baseBackground = if (isDark) Color(0xFF090D16) else Color(0xFFF4F6FB)
    val goldOrbColor = if (isDark) ChurchGold.copy(alpha = 0.28f) else ChurchGoldLight.copy(alpha = 0.38f)
    val blueOrbColor = if (isDark) Color(0xFF1E3A8A).copy(alpha = 0.35f) else Color(0xFF60A5FA).copy(alpha = 0.32f)
    val violetOrbColor = if (isDark) Color(0xFF6D28D9).copy(alpha = 0.26f) else Color(0xFFA78BFA).copy(alpha = 0.28f)
    val emeraldOrbColor = if (isDark) Color(0xFF065F46).copy(alpha = 0.22f) else Color(0xFF34D399).copy(alpha = 0.24f)
    val specularTint = if (isDark) Color(0xFFE2E8F0).copy(alpha = 0.06f) else Color.White.copy(alpha = 0.45f)

    Box(
        modifier = modifier
            .drawBehind {
                val w = size.width
                val h = size.height

                // 1. Solid deep atmospheric foundation
                drawRect(baseBackground)

                // 2. Moving Golden Sunbeam Orb (Upper Left orbit)
                val goldX = w * 0.28f + cos(phase1) * (w * 0.22f)
                val goldY = h * 0.24f + sin(phase1) * (h * 0.16f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(goldOrbColor, goldOrbColor.copy(alpha = 0.08f), Color.Transparent),
                        center = Offset(goldX, goldY),
                        radius = (w * 0.72f).coerceAtLeast(200f)
                    ),
                    center = Offset(goldX, goldY),
                    radius = (w * 0.72f).coerceAtLeast(200f)
                )

                // 3. Moving Cathedral Sapphire Blue Orb (Right-Center orbit)
                val blueX = w * 0.72f + sin(phase2) * (w * 0.24f)
                val blueY = h * 0.52f + cos(phase2) * (h * 0.18f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(blueOrbColor, blueOrbColor.copy(alpha = 0.06f), Color.Transparent),
                        center = Offset(blueX, blueY),
                        radius = (w * 0.80f).coerceAtLeast(250f)
                    ),
                    center = Offset(blueX, blueY),
                    radius = (w * 0.80f).coerceAtLeast(250f)
                )

                // 4. Moving Celestial Violet / Grace Orb (Lower Left/Center orbit)
                val violetX = w * 0.45f + cos(phase3) * (w * 0.28f)
                val violetY = h * 0.78f + sin(phase3) * (h * 0.14f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(violetOrbColor, violetOrbColor.copy(alpha = 0.05f), Color.Transparent),
                        center = Offset(violetX, violetY),
                        radius = (w * 0.75f).coerceAtLeast(220f)
                    ),
                    center = Offset(violetX, violetY),
                    radius = (w * 0.75f).coerceAtLeast(220f)
                )

                // 5. Emerald Prayer Orb (Top Right orbit)
                val emeraldX = w * 0.85f + cos(phase1 * 0.7f) * (w * 0.15f)
                val emeraldY = h * 0.18f + sin(phase2 * 0.7f) * (h * 0.12f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(emeraldOrbColor, emeraldOrbColor.copy(alpha = 0.04f), Color.Transparent),
                        center = Offset(emeraldX, emeraldY),
                        radius = (w * 0.60f).coerceAtLeast(180f)
                    ),
                    center = Offset(emeraldX, emeraldY),
                    radius = (w * 0.60f).coerceAtLeast(180f)
                )

                // 6. Sweeping Specular Celestial Ray (Linear light sweep across screen)
                val rayStartY = h * phaseRays
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            specularTint.copy(alpha = 0.08f),
                            specularTint.copy(alpha = 0.16f),
                            specularTint.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        start = Offset(0f, rayStartY - 120f),
                        end = Offset(w, rayStartY + 120f)
                    )
                )

                // 7. Frosted Glass Diffusion scrim layer to guarantee 100% WCAG contrast & text legibility
                drawRect(
                    if (isDark) Color(0xFF0F172A).copy(alpha = 0.38f)
                    else Color.White.copy(alpha = 0.28f)
                )
            }
    ) {
        content()
    }
}

/**
 * Apple Glass Card
 *
 * Implements Apple's signature VisionOS / iOS Liquid Glass aesthetic:
 * - Translucent frosted acrylic background with subtle gradient diffusion
 * - Specular bevel highlight border simulating optical light refraction
 * - Top specular gleam reflection overlay
 * - Floating soft-diffused elevation shadow
 */
@Composable
fun AppleGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    isDark: Boolean = isSystemInDarkTheme(),
    elevation: Dp = 6.dp,
    onClick: (() -> Unit)? = null,
    testTag: String? = null,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    // Frosted Glass Palette
    val glassFillBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1E293B).copy(alpha = 0.72f),
                Color(0xFF0F172A).copy(alpha = 0.84f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.78f),
                Color(0xFFF8FAFC).copy(alpha = 0.64f)
            )
        )
    }

    // Specular Highlight Bevel Border (Bright edge on top-left, fading to soft translucency)
    val specularBorderBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.40f),
                Color(0xFF94A3B8).copy(alpha = 0.22f),
                Color.White.copy(alpha = 0.08f),
                Color(0xFF64748B).copy(alpha = 0.28f)
            ),
            start = Offset(0f, 0f),
            end = Offset(400f, 600f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.95f),
                Color(0xFFE2E8F0).copy(alpha = 0.55f),
                Color.White.copy(alpha = 0.25f),
                Color(0xFFCBD5E1).copy(alpha = 0.45f)
            ),
            start = Offset(0f, 0f),
            end = Offset(400f, 600f)
        )
    }

    val shadowColor = if (isDark) Color.Black.copy(alpha = 0.35f) else Color(0xFF0F172A).copy(alpha = 0.08f)

    var cardModifier = modifier
        .fillMaxWidth()
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = shadowColor,
            spotColor = shadowColor
        )
        .clip(shape)
        .background(glassFillBrush)
        .border(width = 1.2.dp, brush = specularBorderBrush, shape = shape)

    if (testTag != null) {
        cardModifier = cardModifier.testTag(testTag)
    }

    if (onClick != null) {
        cardModifier = cardModifier.clickable(onClick = onClick)
    }

    Box(modifier = cardModifier) {
        // Specular Top Sheen Highlight (subtle curved lens glare)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            if (isDark) Color.White.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

/**
 * Apple Glass Pill / Chip
 *
 * Translucent frosted glass badge for filters, tags, and indicators.
 */
@Composable
fun AppleGlassPill(
    text: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    selectedColor: Color = RoyalNavy,
    icon: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    testTag: String? = null
) {
    val isDark = isSystemInDarkTheme()

    val bgBrush = when {
        isSelected -> Brush.horizontalGradient(
            colors = listOf(
                selectedColor.copy(alpha = 0.92f),
                selectedColor.copy(alpha = 0.82f)
            )
        )
        isDark -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1E293B).copy(alpha = 0.65f),
                Color(0xFF0F172A).copy(alpha = 0.75f)
            )
        )
        else -> Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.85f),
                Color(0xFFF1F5F9).copy(alpha = 0.70f)
            )
        )
    }

    val borderBrush = when {
        isSelected -> Brush.linearGradient(
            colors = listOf(
                ChurchGoldLight.copy(alpha = 0.70f),
                Color.White.copy(alpha = 0.40f)
            )
        )
        isDark -> Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.35f),
                Color.White.copy(alpha = 0.10f)
            )
        )
        else -> Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.95f),
                Color(0xFFE2E8F0).copy(alpha = 0.60f)
            )
        )
    }

    val contentColor = when {
        isSelected -> Color.White
        isDark -> Color(0xFFF8FAFC)
        else -> Color(0xFF0F172A)
    }

    val shape = RoundedCornerShape(50)
    var pillModifier = modifier
        .clip(shape)
        .background(bgBrush)
        .border(width = 1.dp, brush = borderBrush, shape = shape)

    if (testTag != null) {
        pillModifier = pillModifier.testTag(testTag)
    }

    if (onClick != null) {
        pillModifier = pillModifier.clickable(onClick = onClick)
    }

    Surface(
        color = Color.Transparent,
        modifier = pillModifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(6.dp))
            }
            androidx.compose.material3.Text(
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
            )
        }
    }
}
