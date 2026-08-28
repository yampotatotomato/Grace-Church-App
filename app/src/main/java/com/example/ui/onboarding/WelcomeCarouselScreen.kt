package com.example.ui.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
                accentColor = RoyalNavy
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Horizontal Pager for Carousel
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val step = steps[pageIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Hero Image Banner with gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                ) {
                    Image(
                        painter = painterResource(id = step.imageRes),
                        contentDescription = step.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.25f),
                                        Color.Black.copy(alpha = 0.7f)
                                    )
                                )
                            )
                    )

                    // Floating Icon badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(24.dp)
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(RoyalNavy.copy(alpha = 0.92f))
                            .border(1.5.dp, step.accentColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = step.icon,
                            contentDescription = null,
                            tint = step.accentColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Text Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = step.subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = step.accentColor,
                        letterSpacing = 1.2.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        // Top Navigation: Skip Button
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
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(RoyalNavy),
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
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }

            TextButton(
                onClick = onGetStarted,
                modifier = Modifier.testTag("onboarding_skip_button")
            ) {
                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Bottom Controls: Animated Pagination Dots & Action Button
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Indicator dots
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                repeat(steps.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 28.dp else 8.dp,
                        animationSpec = tween(300),
                        label = "dot_width"
                    )
                    val color = if (isSelected) RoyalNavy else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(width)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                    )
                }
            }

            // Action Button (Next or Get Started)
            Button(
                onClick = {
                    if (isLastPage) {
                        onGetStarted()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag(if (isLastPage) "onboarding_get_started_button" else "onboarding_next_button"),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoyalNavy,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isLastPage) "Begin Journey" else "Continue",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = CupertinoIcons.ArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
