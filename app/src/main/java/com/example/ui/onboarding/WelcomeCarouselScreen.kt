package com.example.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.CupertinoIcons
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.abs

data class OnboardingStep(
    val title: String,
    val subtitle: String,
    val description: String,
    val imageRes: Int,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun WelcomeCarouselScreen(
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val steps = remember {
        listOf(
            OnboardingStep(
                title = "Welcome to Grace Sanctuary",
                subtitle = "FAITH • FELLOWSHIP • SCRIPTURE",
                description = "Your spiritual home and sanctuary in the palm of your hand. Experience Christ-centered worship, growth, and community wherever you go.",
                imageRes = R.drawable.img_onboarding_welcome,
                icon = CupertinoIcons.HouseFill,
                accentColor = ChurchGold
            ),
            OnboardingStep(
                title = "Daily Scripture & Devotions",
                subtitle = "GROW IN THE WORD",
                description = "Dive into Holy Scripture with custom reader modes, audio sermon reflections, and favorited daily devotionals.",
                imageRes = R.drawable.img_onboarding_scripture,
                icon = CupertinoIcons.Book,
                accentColor = ScriptureAccent
            ),
            OnboardingStep(
                title = "Spiritual Journaling",
                subtitle = "SANCTUARY REFLECTIONS",
                description = "Keep a personal, private journal of your spiritual journey, answered prayers, devotion reflections, and heartfelt gratitude.",
                imageRes = R.drawable.img_onboarding_welcome,
                icon = CupertinoIcons.SquareAndPencil,
                accentColor = DevotionAccent
            ),
            OnboardingStep(
                title = "Local Prayer Groups & Fellowship",
                subtitle = "UNITED IN PRAYER",
                description = "Find prayer fellowships in your area, RSVP for weekly meetings, contact pastoral counselors, and share prayer requests.",
                imageRes = R.drawable.img_onboarding_community,
                icon = CupertinoIcons.Person2Fill,
                accentColor = PrayerAccent
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { steps.size })
    val isLastPage = pagerState.currentPage == steps.size - 1

    // Infinite bouncing animation for the vertical scroll guide hint
    val infiniteTransition = rememberInfiniteTransition(label = "scroll_bounce")
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Vertical Pager for Smooth Top-to-Bottom Scrolling
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val step = steps[pageIndex]

            // Calculate exact offset distance for this page from the viewport center (-1f to 1f)
            val pageOffset = (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
            val absOffset = abs(pageOffset).coerceIn(0f, 1f)

            // Smooth animated parameters for image, texts, and badges
            val imageScale = 1f - (absOffset * 0.12f)
            val imageTranslationY = pageOffset * 90f
            val imageAlpha = (1f - (absOffset * 0.35f)).coerceIn(0.2f, 1f)

            val textTranslationY = pageOffset * 65f
            val textAlpha = (1f - (absOffset * 1.6f)).coerceIn(0f, 1f)
            val textScale = 1f - (absOffset * 0.08f)

            val badgeScale = (1f - (absOffset * 0.25f)).coerceIn(0.5f, 1.2f)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = 1f - (absOffset * 0.15f)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Hero Image Banner with gradient scrim & dynamic parallax
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.15f)
                        .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
                        .shadow(12.dp, RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
                ) {
                    Image(
                        painter = painterResource(id = step.imageRes),
                        contentDescription = step.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = imageScale
                                scaleY = imageScale
                                translationY = imageTranslationY
                                alpha = imageAlpha
                            }
                    )

                    // Cinematic Gradient Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.3f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.75f)
                                    )
                                )
                            )
                    )

                    // Floating Icon badge with dynamic zoom and glow
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 24.dp, bottom = 24.dp)
                            .size(60.dp)
                            .graphicsLayer {
                                scaleX = badgeScale
                                scaleY = badgeScale
                                translationY = -pageOffset * 30f
                            }
                            .clip(CircleShape)
                            .background(RoyalNavy.copy(alpha = 0.94f))
                            .border(2.dp, step.accentColor, CircleShape)
                            .shadow(8.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = step.icon,
                            contentDescription = null,
                            tint = step.accentColor,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    // Vertical Progress pill tag on top-right of image
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .statusBarsPadding()
                            .padding(end = 20.dp, top = 16.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.45f)
                    ) {
                        Text(
                            text = "${pageIndex + 1} / ${steps.size}",
                            style = AppleTypographyStyles.referenceTag,
                            color = Color.White,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Text Content with Staggered Fade & Slide Animations
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.95f)
                        .padding(horizontal = 28.dp)
                        .graphicsLayer {
                            translationY = textTranslationY
                            alpha = textAlpha
                            scaleX = textScale
                            scaleY = textScale
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    // Subtitle (SF Mono tag style)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = step.accentColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = step.subtitle,
                            style = AppleTypographyStyles.referenceTag,
                            color = step.accentColor,
                            fontSize = 11.sp,
                            letterSpacing = 1.2.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Main Title (New York Serif Display)
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.displayMedium,
                        fontFamily = AppleFontFamilies.NewYorkSerif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp,
                        fontSize = 24.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description Body (SF Pro)
                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        fontSize = 15.sp
                    )

                    // Vertical swipe cue on non-last pages
                    if (!isLastPage) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .graphicsLayer { translationY = bounceOffset }
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(
                                            pageIndex + 1,
                                            animationSpec = spring(
                                                stiffness = Spring.StiffnessMediumLow,
                                                dampingRatio = Spring.DampingRatioLowBouncy
                                            )
                                        )
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Swipe up for next",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = step.accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Space for bottom button bar
                Spacer(modifier = Modifier.height(96.dp))
            }
        }

        // Top Navigation: Grace Sanctuary Brand & Skip Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(RoyalNavy.copy(alpha = 0.85f))
                        .border(1.dp, ChurchGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = CupertinoIcons.Sparkles,
                        contentDescription = null,
                        tint = ChurchGold,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "GRACE SANCTUARY",
                    style = AppleTypographyStyles.referenceTag,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.35f)
            ) {
                TextButton(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .testTag("onboarding_skip_button")
                        .padding(horizontal = 4.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Skip",
                        style = AppleTypographyStyles.uiButton,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Right-Side Vertical Indicator Dots
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(steps.size) { index ->
                val isSelected = pagerState.currentPage == index
                val height by animateDpAsState(
                    targetValue = if (isSelected) 28.dp else 8.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "v_dot_height"
                )
                val dotColor by animateColorAsState(
                    targetValue = if (isSelected) steps[pagerState.currentPage].accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                    animationSpec = tween(250),
                    label = "v_dot_color"
                )

                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(height)
                        .clip(RoundedCornerShape(3.dp))
                        .background(dotColor)
                        .clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    index,
                                    animationSpec = spring(
                                        stiffness = Spring.StiffnessMediumLow,
                                        dampingRatio = Spring.DampingRatioLowBouncy
                                    )
                                )
                            }
                        }
                )
            }
        }

        // Bottom Controls: Action Button (Continue or Begin Journey)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 18.dp),
            color = Color.Transparent
        ) {
            val activeColor = steps[pagerState.currentPage].accentColor

            Button(
                onClick = {
                    if (isLastPage) {
                        onGetStarted()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                pagerState.currentPage + 1,
                                animationSpec = spring(
                                    stiffness = Spring.StiffnessMediumLow,
                                    dampingRatio = Spring.DampingRatioLowBouncy
                                )
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(10.dp, RoundedCornerShape(27.dp))
                    .testTag(if (isLastPage) "onboarding_get_started_button" else "onboarding_next_button"),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLastPage) activeColor else RoyalNavy,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isLastPage) "Begin Sanctuary Journey" else "Continue",
                        style = AppleTypographyStyles.uiButton,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (isLastPage) CupertinoIcons.Sparkles else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
