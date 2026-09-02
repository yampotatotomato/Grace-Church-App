package com.example.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.ChurchTab
import com.example.ui.ChurchUiState
import com.example.ui.ChurchViewModel
import com.example.ui.components.CupertinoIcons
import com.example.ui.components.IosGroupedCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.abs

data class OnboardingFeatureHighlight(
    val icon: ImageVector,
    val title: String,
    val description: String
)

data class OnboardingStepData(
    val stepIndex: Int,
    val title: String,
    val subtitle: String,
    val tag: String,
    val description: String,
    val imageRes: Int,
    val icon: ImageVector,
    val accentColor: Color,
    val associatedTab: ChurchTab,
    val highlights: List<OnboardingFeatureHighlight>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeCarouselScreen(
    viewModel: ChurchViewModel,
    isReviewMode: Boolean = false,
    onGetStarted: (ChurchTab?) -> Unit,
    onDismissReview: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    val steps = remember {
        listOf(
            OnboardingStepData(
                stepIndex = 1,
                title = "Welcome to Church App",
                subtitle = "FAITH • FELLOWSHIP • SCRIPTURE",
                tag = "SANCTUARY & TODAY",
                description = "Your digital spiritual home and community sanctuary. Experience Christ-centered worship, verse of the day, daily audio devotionals, and live church bulletins wherever you are.",
                imageRes = R.drawable.img_onboarding_welcome,
                icon = CupertinoIcons.Sparkles,
                accentColor = ChurchGold,
                associatedTab = ChurchTab.HOME,
                highlights = listOf(
                    OnboardingFeatureHighlight(
                        icon = CupertinoIcons.HouseFill,
                        title = "Sanctuary Daily Dashboard",
                        description = "Start every day with inspirational scripture reflections and church community bulletins."
                    ),
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.Headphones,
                        title = "Daily Audio Commentary",
                        description = "Listen to uplifting devotional audio streams with playback and speed controls."
                    ),
                    OnboardingFeatureHighlight(
                        icon = CupertinoIcons.Megaphone,
                        title = "Announcements & Events",
                        description = "Stay informed on worship services, outreach programs, and ministry updates."
                    )
                )
            ),
            OnboardingStepData(
                stepIndex = 2,
                title = "Holy Scripture & Bible Study",
                subtitle = "GROW IN GOD'S LIVING WORD",
                tag = "BIBLE & TRANSLATIONS",
                description = "Immerse yourself daily in the Holy Bible with a distraction-free, elegant reading experience designed for deep contemplation and study.",
                imageRes = R.drawable.img_onboarding_scripture,
                icon = CupertinoIcons.Book,
                accentColor = ScriptureAccent,
                associatedTab = ChurchTab.SCRIPTURE,
                highlights = listOf(
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.Translate,
                        title = "Multi-Translation Switcher",
                        description = "Instantly toggle between NIV, ESV, KJV, and NLT translations with parallel verse views."
                    ),
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.BookmarkBorder,
                        title = "Verse Bookmarks & Favorites",
                        description = "Save meaningful verses to your personal bookmarks bank and share them with loved ones."
                    ),
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.FormatSize,
                        title = "Customizable Typography",
                        description = "Personalize font sizing, line spacing, and reading presets for comfort."
                    )
                )
            ),
            OnboardingStepData(
                stepIndex = 3,
                title = "Daily Devotions & Reflections",
                subtitle = "SPIRITUAL NOURISHMENT & JOURNAL",
                tag = "DEVOTIONS & JOURNAL",
                description = "Cultivate a meaningful habit of daily quiet time with God. Track your spiritual streak and document prayers, devotional notes, and gratitude.",
                imageRes = R.drawable.img_onboarding_journal,
                icon = CupertinoIcons.HeartFill,
                accentColor = DevotionAccent,
                associatedTab = ChurchTab.DEVOTION,
                highlights = listOf(
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.LocalFireDepartment,
                        title = "Daily Faith Streaks",
                        description = "Celebrate consistency in God's presence and track your spiritual growth milestones."
                    ),
                    OnboardingFeatureHighlight(
                        icon = CupertinoIcons.SquareAndPencil,
                        title = "Private Spiritual Journal",
                        description = "Reflect on devotions with 100% private, on-device notes, prayers, and gratitude entries."
                    ),
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.FilterList,
                        title = "Structured Categories",
                        description = "Organize insights into Devotions, Prayers, Gratitude, and Personal Reflections."
                    )
                )
            ),
            OnboardingStepData(
                stepIndex = 4,
                title = "Community & Pastoral Care",
                subtitle = "UNITED IN PRAYER & LOVE",
                tag = "FELLOWSHIP & CARE",
                description = "Connect with fellow believers across regional prayer groups, submit confidential prayer requests, and receive biblical guidance from ordained pastors.",
                imageRes = R.drawable.img_onboarding_community,
                icon = CupertinoIcons.Person2Fill,
                accentColor = PrayerAccent,
                associatedTab = ChurchTab.COMMUNITY,
                highlights = listOf(
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.LocationOn,
                        title = "Regional Prayer Groups",
                        description = "Join campus and home Bible study groups across North, South, East, West, and Online."
                    ),
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.VolunteerActivism,
                        title = "Community Prayer Wall",
                        description = "Share prayer requests confidentially or publicly and intercede for church family members."
                    ),
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.Shield,
                        title = "Pastoral Directory & Guidance",
                        description = "Reach pastors directly for confidential counseling and stream expository sermons."
                    )
                )
            ),
            OnboardingStepData(
                stepIndex = 5,
                title = "Personalize Your Experience",
                subtitle = "CUSTOMIZE YOUR FAITH ROUTINE",
                tag = "PREFERENCES & SETUP",
                description = "Set your preferred Bible translation, morning scripture reminder time, and Sanctuary color theme for a personalized spiritual journey.",
                imageRes = R.drawable.img_onboarding_pastoral,
                icon = Icons.Default.Tune,
                accentColor = ChurchGold,
                associatedTab = ChurchTab.HOME,
                highlights = emptyList() // Interactive Setup UI
            )
        )
    }

    val totalSteps = steps.size
    val pagerState = rememberPagerState(pageCount = { totalSteps })
    val isLastPage = pagerState.currentPage == totalSteps - 1
    val currentStep = steps[pagerState.currentPage]

    var selectedSpiritualFocus by remember { mutableStateOf("Daily Scripture") }

    val translations = listOf("NIV", "ESV", "KJV", "NLT")
    val reminderTimes = listOf("06:00 AM", "07:00 AM", "08:00 AM", "09:00 AM")
    val focusOptions = listOf(
        Pair("Daily Scripture", ChurchTab.SCRIPTURE),
        Pair("Devotions", ChurchTab.DEVOTION),
        Pair("Prayer & Community", ChurchTab.COMMUNITY),
        Pair("Sanctuary Today", ChurchTab.HOME)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // TOP APP BAR: Step Progress & Skip/Close Button
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Brand / Mode Identifier
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(RoyalNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = currentStep.icon,
                                contentDescription = null,
                                tint = currentStep.accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isReviewMode) "FEATURE WALKTHROUGH" else "CHURCH APP",
                                style = AppleTypographyStyles.referenceTag,
                                fontWeight = FontWeight.Bold,
                                color = RoyalNavy,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Step ${pagerState.currentPage + 1} of $totalSteps",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Skip / Close Review Button
                    if (isReviewMode) {
                        FilledTonalButton(
                            onClick = onDismissReview,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("onboarding_close_review_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Walkthrough",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Exit Tour", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        TextButton(
                            onClick = { onGetStarted(null) },
                            modifier = Modifier.testTag("onboarding_skip_button")
                        ) {
                            Text(
                                text = "Skip",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Animated Step Progress Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(totalSteps) { index ->
                        val isCurrent = pagerState.currentPage == index
                        val isPassed = pagerState.currentPage > index
                        val barColor by animateColorAsState(
                            targetValue = when {
                                isCurrent -> currentStep.accentColor
                                isPassed -> RoyalNavy
                                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                            },
                            animationSpec = tween(300),
                            label = "step_bar_color"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(barColor)
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                        )
                    }
                }
            }
        }

        // MAIN CONTENT PAGER
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { pageIndex ->
            val step = steps[pageIndex]

            val pageOffset = (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
            val absOffset = abs(pageOffset).coerceIn(0f, 1f)

            val contentAlpha = (1f - (absOffset * 1.5f)).coerceIn(0.2f, 1f)
            val contentScale = 1f - (absOffset * 0.05f)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = contentAlpha
                        scaleX = contentScale
                        scaleY = contentScale
                    }
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // Hero Image Card with Gradient & Floating Badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .shadow(8.dp, RoundedCornerShape(22.dp))
                ) {
                    Image(
                        painter = painterResource(id = step.imageRes),
                        contentDescription = step.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.2f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.82f)
                                    )
                                )
                            )
                    )

                    // Top Right Category Pill
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(14.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = step.tag,
                            style = AppleTypographyStyles.referenceTag,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Floating Icon & Subtitle Banner at bottom of Image
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(RoyalNavy)
                                .border(1.5.dp, step.accentColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = step.icon,
                                contentDescription = null,
                                tint = step.accentColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = step.subtitle,
                                style = AppleTypographyStyles.referenceTag,
                                color = ChurchGoldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = step.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Step Description Body
                Text(
                    text = step.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // If Step 1 to 4: Display Feature Highlights
                if (step.highlights.isNotEmpty()) {
                    Text(
                        text = "CORE CAPABILITIES & HIGHLIGHTS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = step.accentColor,
                        letterSpacing = 1.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        step.highlights.forEach { highlight ->
                            IosGroupedCard(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(step.accentColor.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = highlight.icon,
                                            contentDescription = null,
                                            tint = step.accentColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = highlight.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = highlight.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // If Step 5: Interactive Personalization Form
                if (pageIndex == 4) {
                    PersonalizationStepWidgets(
                        uiState = uiState,
                        viewModel = viewModel,
                        translations = translations,
                        reminderTimes = reminderTimes,
                        focusOptions = focusOptions,
                        selectedFocus = selectedSpiritualFocus,
                        onSelectFocus = { selectedSpiritualFocus = it }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // BOTTOM ACTION CONTROLS
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button (hidden on first page)
                if (pagerState.currentPage > 0) {
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("onboarding_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Step",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Back", style = MaterialTheme.typography.labelMedium)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // Forward / Complete Action Button
                val buttonColor = if (isLastPage) ChurchGold else RoyalNavy
                Button(
                    onClick = {
                        if (isLastPage) {
                            val targetTab = focusOptions.find { it.first == selectedSpiritualFocus }?.second
                            onGetStarted(targetTab)
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor,
                        contentColor = if (isLastPage) RoyalNavy else Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    modifier = Modifier.testTag(if (isLastPage) "onboarding_get_started_button" else "onboarding_next_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isLastPage) {
                                if (isReviewMode) "Complete Review & Enter" else "Enter Church App"
                            } else "Next Step",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (isLastPage) CupertinoIcons.Sparkles else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PersonalizationStepWidgets(
    uiState: ChurchUiState,
    viewModel: ChurchViewModel,
    translations: List<String>,
    reminderTimes: List<String>,
    focusOptions: List<Pair<String, ChurchTab>>,
    selectedFocus: String,
    onSelectFocus: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Primary Spiritual Focus Selection
        Text(
            text = "1. WHAT IS YOUR PRIMARY SPIRITUAL FOCUS?",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = ChurchGold,
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            focusOptions.forEach { (label, _) ->
                val isSelected = selectedFocus == label
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectFocus(label) },
                    label = {
                        Text(
                            text = label,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalNavy,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("onboarding_focus_${label.lowercase().replace(" ", "_")}")
                )
            }
        }

        // 2. Preferred Bible Translation
        Text(
            text = "2. PREFERRED BIBLE TRANSLATION",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = ChurchGold,
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            translations.forEach { trans ->
                val isSelected = uiState.selectedTranslation == trans
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) RoyalNavy else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) RoyalNavy else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.setTranslation(trans) }
                        .testTag("onboarding_translation_$trans")
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = trans,
                            style = AppleTypographyStyles.referenceTag,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 3. Daily Morning Verse Alert Time
        Text(
            text = "3. DAILY MORNING SCRIPTURE ALERT TIME",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = ChurchGold,
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            reminderTimes.forEach { time ->
                val isSelected = uiState.dailyVerseTime == time
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setDailyVerseTime(time) },
                    label = {
                        Text(
                            text = time,
                            style = AppleTypographyStyles.referenceTag,
                            fontSize = 11.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ChurchGold.copy(alpha = 0.25f),
                        selectedLabelColor = RoyalNavy
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Sanctuary Theme & Color Tone
        Text(
            text = "4. SANCTUARY COLOR ACCENT",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = ChurchGold,
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AccentTheme.values().forEach { accent ->
                val isSelected = uiState.accentTheme == accent
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) accent.accentColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) accent.accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.setAccentTheme(accent) }
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(accent.accentColor)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = accent.title.split(" ").first(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
