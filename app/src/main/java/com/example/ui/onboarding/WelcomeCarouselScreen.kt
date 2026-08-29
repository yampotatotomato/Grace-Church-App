package com.example.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
                title = "Welcome to Grace Sanctuary",
                subtitle = "FAITH • FELLOWSHIP • SCRIPTURE",
                tag = "SANCTUARY OVERVIEW",
                description = "Your digital spiritual home and community sanctuary. Experience Christ-centered worship, scripture reading, prayer fellowships, and pastoral counseling wherever life takes you.",
                imageRes = R.drawable.img_onboarding_welcome,
                icon = CupertinoIcons.Sparkles,
                accentColor = ChurchGold,
                associatedTab = ChurchTab.DEVOTION,
                highlights = listOf(
                    OnboardingFeatureHighlight(
                        icon = CupertinoIcons.Book,
                        title = "Holy Scripture & Expository Study",
                        description = "NIV, ESV, KJV, and NLT translations with customizable typography."
                    ),
                    OnboardingFeatureHighlight(
                        icon = CupertinoIcons.Person2Fill,
                        title = "Local Prayer Fellowships",
                        description = "Connect with believers in regional campuses and online prayer circles."
                    ),
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.Shield,
                        title = "Pastoral Care & Guidance",
                        description = "Direct, confidential pastoral inquiries and sermon archives."
                    )
                )
            ),
            OnboardingStepData(
                stepIndex = 2,
                title = "Holy Scripture & Devotionals",
                subtitle = "GROW IN GOD'S WORD",
                tag = "SCRIPTURE & STUDY",
                description = "Immerse yourself daily in the living Word of God with an elegant reading experience designed for focused contemplation.",
                imageRes = R.drawable.img_onboarding_scripture,
                icon = CupertinoIcons.Book,
                accentColor = ScriptureAccent,
                associatedTab = ChurchTab.SCRIPTURE,
                highlights = listOf(
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.Translate,
                        title = "Multi-Translation Switcher",
                        description = "Instantly toggle between NIV, ESV, KJV, and NLT with verse parallel view."
                    ),
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.BookmarkBorder,
                        title = "Verse Favoriting & Sharing",
                        description = "Save meaningful scriptures to your favorites bank and share them with loved ones."
                    ),
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.Headphones,
                        title = "Daily Guided Devotionals",
                        description = "Listen to expository audio commentary and reflect on the Verse of the Day."
                    )
                )
            ),
            OnboardingStepData(
                stepIndex = 3,
                title = "Community & Prayer Groups",
                subtitle = "UNITED IN PRAYER & LOVE",
                tag = "FELLOWSHIP & PRAYER",
                description = "Find deep Christian community in regional campuses across North, South, East, West, and Online fellowships.",
                imageRes = R.drawable.img_onboarding_community,
                icon = CupertinoIcons.Person2Fill,
                accentColor = PrayerAccent,
                associatedTab = ChurchTab.COMMUNITY,
                highlights = listOf(
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.LocationOn,
                        title = "Campus & Area Filters",
                        description = "Browse weekly small groups, home Bible studies, and young adult circles."
                    ),
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.EventAvailable,
                        title = "1-Tap Meeting RSVPs",
                        description = "Confirm attendance for weekly gatherings and receive timely reminders."
                    ),
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.VolunteerActivism,
                        title = "Prayer Request Wall",
                        description = "Share prayer needs confidentially or publicly and pray for church family members."
                    )
                )
            ),
            OnboardingStepData(
                stepIndex = 4,
                title = "Pastoral Care & Counseling",
                subtitle = "SPIRITUAL SHEPHERDING & SERMONS",
                tag = "PASTORAL DIRECTORY",
                description = "Our ordained pastors and ministry leaders are dedicated to walking with you through life decisions, grief, prayer, and biblical guidance.",
                imageRes = R.drawable.img_onboarding_pastoral,
                icon = Icons.Default.Shield,
                accentColor = PastorAccent,
                associatedTab = ChurchTab.SERMONS,
                highlights = listOf(
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.Badge,
                        title = "Pastoral Directory & Profiles",
                        description = "Learn about pastoral specialties in senior leadership, counseling, and youth."
                    ),
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.MailOutline,
                        title = "Direct Guidance Requests",
                        description = "Submit confidential spiritual questions and receive biblical counsel & anchor verses."
                    ),
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.GraphicEq,
                        title = "Expository Sermon Library",
                        description = "Stream recent Sunday sermon audio with speed control, key points, and study notes."
                    )
                )
            ),
            OnboardingStepData(
                stepIndex = 5,
                title = "Spiritual Journaling & Gratitude",
                subtitle = "DOCUMENT YOUR SPIRITUAL WALK",
                tag = "SANCTUARY JOURNAL",
                description = "Cultivate a meaningful habit of private reflection, recording answered prayers, devotional notes, and spiritual gratitude.",
                imageRes = R.drawable.img_onboarding_journal,
                icon = CupertinoIcons.SquareAndPencil,
                accentColor = DevotionAccent,
                associatedTab = ChurchTab.JOURNAL,
                highlights = listOf(
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.EditNote,
                        title = "Structured Reflection Categories",
                        description = "Organize entries into Devotional Insights, Prayers, Gratitude, and Reflections."
                    ),
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.Lock,
                        title = "100% Private On-Device",
                        description = "Your personal prayers and journals remain completely private on your phone."
                    ),
                    OnboardingFeatureHighlight(
                        icon = Icons.Default.LocalFireDepartment,
                        title = "Daily Devotional Streaks",
                        description = "Track your daily consistency in God's presence and celebrate faith milestones."
                    )
                )
            ),
            OnboardingStepData(
                stepIndex = 6,
                title = "Personalize Your Sanctuary",
                subtitle = "CUSTOMIZE YOUR FAITH ROUTINE",
                tag = "PREFERENCES SETUP",
                description = "Tailor your Bible translation, daily morning scripture alerts, and ministry focus for an experience uniquely crafted for your walk with God.",
                imageRes = R.drawable.img_onboarding_welcome,
                icon = Icons.Default.Tune,
                accentColor = ChurchGold,
                associatedTab = ChurchTab.DEVOTION,
                highlights = emptyList() // Will show interactive setup widgets
            )
        )
    }

    val totalSteps = steps.size
    val pagerState = rememberPagerState(pageCount = { totalSteps })
    val isLastPage = pagerState.currentPage == totalSteps - 1
    val currentStep = steps[pagerState.currentPage]

    // Selected spiritual preference state for step 6
    var selectedSpiritualFocus by remember { mutableStateOf("Daily Scripture") }

    val translations = listOf("NIV", "ESV", "KJV", "NLT")
    val reminderTimes = listOf("06:00 AM", "07:00 AM", "08:00 AM", "09:00 AM")
    val focusOptions = listOf(
        Pair("Daily Scripture", ChurchTab.SCRIPTURE),
        Pair("Prayer Fellowships", ChurchTab.COMMUNITY),
        Pair("Pastoral Care", ChurchTab.SERMONS),
        Pair("Spiritual Journal", ChurchTab.JOURNAL)
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
                                .size(32.dp)
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
                                text = if (isReviewMode) "FEATURE WALKTHROUGH" else "GRACE SANCTUARY",
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
                                        Color.Black.copy(alpha = 0.8f)
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
                        color = Color.Black.copy(alpha = 0.55f)
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

                // If Step 1 to 5: Display Feature Highlights
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

                // If Step 6: Interactive Personalization Form
                if (pageIndex == 5) {
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
                                if (isReviewMode) "Complete Review & Enter" else "Enter Grace Sanctuary"
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
    uiState: com.example.ui.ChurchUiState,
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
            focusOptions.forEach { (label, tab) ->
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
                    border = androidx.compose.foundation.BorderStroke(
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
                    border = androidx.compose.foundation.BorderStroke(
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
