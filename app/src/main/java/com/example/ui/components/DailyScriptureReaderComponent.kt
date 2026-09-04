package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookmarkEntity
import com.example.data.model.DailyVerse
import com.example.data.repository.ChurchDataSeed
import com.example.ui.ChurchTab
import com.example.ui.ChurchUiState
import com.example.ui.ChurchViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Feature-rich Scripture Reader Component that fetches daily verses and displays them
 * with interactive translation switching, adjustable typography, audio recitation,
 * and comprehensive Room-backed bookmarking capabilities (including personal notes and bookmarks sheet).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyScriptureReaderComponent(
    viewModel: ChurchViewModel,
    onNavigateToChapter: (bookName: String, chapter: Int, verse: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()

    val currentVerse = uiState.currentDailyVerse
    val translation = uiState.selectedTranslation

    // Check if current verse is bookmarked in Room DB
    val isCurrentVerseBookmarked = remember(bookmarks, currentVerse) {
        bookmarks.any {
            it.book.equals(currentVerse.book, ignoreCase = true) &&
            it.chapter == currentVerse.chapter &&
            it.verse == currentVerse.verse
        }
    }

    // Existing bookmark for this verse (if any) to show existing note
    val existingBookmark = remember(bookmarks, currentVerse) {
        bookmarks.find {
            it.book.equals(currentVerse.book, ignoreCase = true) &&
            it.chapter == currentVerse.chapter &&
            it.verse == currentVerse.verse
        }
    }

    var isReflectionExpanded by remember { mutableStateOf(false) }
    var showBookmarksSheet by remember { mutableStateOf(false) }
    var showNoteDialog by remember { mutableStateOf(false) }
    var showGoalSettingsDialog by remember { mutableStateOf(false) }
    var showManualCelebrationDialog by remember { mutableStateOf(false) }
    var noteInputText by remember { mutableStateOf("") }
    var localFontSizeSp by remember { mutableFloatStateOf(uiState.readerFontSizeSp) }
    var isShuffleRotating by remember { mutableStateOf(false) }

    // Reading Goal & Checkmark State
    val isCurrentVerseRead = remember(uiState.completedVerseKeysToday, currentVerse.reference) {
        uiState.completedVerseKeysToday.contains(currentVerse.reference)
    }
    val completedCount = uiState.completedVerseKeysToday.size
    val goalTarget = uiState.dailyReadingGoalTarget
    val readingProgressFraction = (completedCount.toFloat() / goalTarget.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val isDailyGoalReached = completedCount >= goalTarget

    val animatedGoalProgress by animateFloatAsState(
        targetValue = readingProgressFraction,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "reading_goal_progress"
    )

    // Text to display based on selected translation
    val verseText = remember(currentVerse, translation) {
        currentVerse.translationTexts[translation] ?: currentVerse.text
    }

    val themes = remember {
        listOf(
            "All",
            "God's Love",
            "Peace & Anxiety",
            "Strength",
            "Wisdom & Trust",
            "Faith & Hope",
            "Courage",
            "Sanctuary Rest",
            "Grace"
        )
    }

    val translations = remember { listOf("NIV", "ESV", "KJV", "NLT") }

    val rotationAngle by animateFloatAsState(
        targetValue = if (isShuffleRotating) 360f else 0f,
        animationSpec = tween(durationMillis = 500),
        finishedListener = { isShuffleRotating = false },
        label = "shuffle_rotation"
    )

    var isGlassAtmosphereEnabled by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .shadow(
                elevation = if (isGlassAtmosphereEnabled) 8.dp else 2.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .testTag("scripture_reader_component")
    ) {
        if (isGlassAtmosphereEnabled) {
            AppleMovingAtmosphereBackground(
                modifier = Modifier.matchParentSize()
            ) {}
        }

        AppleGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = 0.dp,
            contentPadding = PaddingValues(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
            // 1. TOP HEADER: Badge, Fetch Controls, and Bookmarks Shortcut
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Section Title Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(ChurchGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = CupertinoIcons.Sparkles,
                            contentDescription = null,
                            tint = ChurchGold,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "DAILY SCRIPTURE READER",
                            style = AppleTypographyStyles.referenceTag,
                            color = ChurchGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        )
                        Text(
                            text = currentVerse.date,
                            style = AppleTypographyStyles.audioTimer,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                // Quick Action Buttons: Apple Glass indicator + Saved Bookmarks count pill + Shuffle Fetch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Apple Glass Atmosphere Indicator & Toggle
                    AppleGlassPill(
                        text = if (isGlassAtmosphereEnabled) "✨ Glass" else "Static",
                        isSelected = isGlassAtmosphereEnabled,
                        selectedColor = ChurchGold,
                        onClick = { isGlassAtmosphereEnabled = !isGlassAtmosphereEnabled },
                        testTag = "scripture_reader_toggle_glass_btn"
                    )

                    // Saved Bookmarks Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (bookmarks.isNotEmpty()) ScriptureAccent.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showBookmarksSheet = true }
                            .testTag("scripture_reader_view_bookmarks_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = CupertinoIcons.BookmarkFill,
                                contentDescription = "View Saved Bookmarks",
                                tint = if (bookmarks.isNotEmpty()) ScriptureAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${bookmarks.size}",
                                style = AppleTypographyStyles.referenceTag,
                                color = if (bookmarks.isNotEmpty()) ScriptureAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Fetch / Shuffle Button
                    IconButton(
                        onClick = {
                            isShuffleRotating = true
                            viewModel.fetchRandomDailyVerse()
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("scripture_reader_shuffle_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Fetch Another Daily Verse",
                            tint = ChurchGold,
                            modifier = Modifier
                                .size(18.dp)
                                .rotate(rotationAngle)
                        )
                    }

                    // Previous / Next Day Fetch Buttons
                    IconButton(
                        onClick = { viewModel.fetchPreviousDailyVerse() },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("scripture_reader_prev_verse_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous Daily Verse",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.fetchNextDailyVerse() },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("scripture_reader_next_verse_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next Daily Verse",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. THEME FILTER CHIPS ROW (Fetch by Theme)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                themes.forEach { themeName ->
                    val isSelected = uiState.dailyVerseThemeFilter.equals(themeName, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.filterDailyVerseByTheme(themeName)
                        },
                        label = {
                            Text(
                                text = themeName,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RoyalNavy,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.height(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2.5 VISUAL PROGRESS BAR & CHECKMARK GOAL TRACKER
            DailyReadingGoalTrackerSection(
                completedCount = completedCount,
                goalTarget = goalTarget,
                progressFraction = animatedGoalProgress,
                streakDays = uiState.readingStreakDays,
                isGoalReached = isDailyGoalReached,
                onOpenGoalSettings = { showGoalSettingsDialog = true },
                onCelebrate = { showManualCelebrationDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("scripture_reading_goal_tracker_section")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3. FETCHING LOADING STATE / SCRIPTURE CARD CONTENT
            AnimatedContent(
                targetState = uiState.isFetchingDailyVerse,
                transitionSpec = {
                    fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(150))
                },
                label = "fetching_verse_anim"
            ) { isFetching ->
                if (isFetching) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = ChurchGold,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Fetching Daily Scripture...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                } else {
                    // SCRIPTURE PASSAGE DISPLAY
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Ornamental Vertical Accent Bar
                            Box(
                                modifier = Modifier
                                    .width(3.5.dp)
                                    .height(70.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(ChurchGold, ScriptureAccent)
                                        )
                                    )
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "“$verseText”",
                                    style = AppleTypographyStyles.scriptureText(localFontSizeSp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = (localFontSizeSp * 1.55f).sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "— ${currentVerse.reference}",
                                        style = AppleTypographyStyles.referenceTag,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    )

                                    if (currentVerse.theme.isNotBlank()) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = ScriptureAccent.copy(alpha = 0.1f)
                                        ) {
                                            Text(
                                                text = currentVerse.theme,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = ScriptureAccent,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 10.5.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Visual Checkmark Button for Current Verse
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isCurrentVerseRead) Color(0xFF2E7D32).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isCurrentVerseRead) Color(0xFF2E7D32).copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                                        ),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                viewModel.toggleVerseReadingCompleted(currentVerse.reference)
                                            }
                                            .testTag("scripture_reader_verse_check_btn")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isCurrentVerseRead) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                                contentDescription = if (isCurrentVerseRead) "Completed Reading" else "Mark as Read",
                                                tint = if (isCurrentVerseRead) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(17.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isCurrentVerseRead) "Completed Reading ✓" else "Mark as Read",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isCurrentVerseRead) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.5.sp
                                            )
                                        }
                                    }

                                    if (isCurrentVerseRead) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.DoneAll,
                                                contentDescription = null,
                                                tint = Color(0xFF2E7D32),
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Goal counted",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF2E7D32),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Existing Note Pill (if this verse was already bookmarked with a note)
                        if (existingBookmark != null && existingBookmark.note.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = ChurchGold.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, ChurchGold.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        noteInputText = existingBookmark.note
                                        showNoteDialog = true
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EditNote,
                                        contentDescription = null,
                                        tint = ChurchGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Note: \"${existingBookmark.note}\"",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "Edit",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ChurchGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recitation Progress Bar (active when reciting verse)
            if (uiState.isDailyVerseReciting) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = ChurchGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Reciting ${currentVerse.reference} ($translation)...",
                                style = AppleTypographyStyles.referenceTag,
                                color = ChurchGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "${(uiState.dailyVerseRecitationProgress * 100).toInt()}%",
                            style = AppleTypographyStyles.audioTimer,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { uiState.dailyVerseRecitationProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = ChurchGold,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            // 4. EXPANDABLE PONDER & GUIDED PRAYER CARD
            AnimatedVisibility(
                visible = isReflectionExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                        .padding(14.dp)
                ) {
                    // Ponder section
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = CupertinoIcons.HeartFill,
                            contentDescription = null,
                            tint = DevotionAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PONDER & REFLECT",
                            style = AppleTypographyStyles.referenceTag,
                            color = DevotionAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentVerse.reflection,
                        style = AppleTypographyStyles.devotionalProse(13.5f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Guided Prayer section
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = CupertinoIcons.Sparkles,
                            contentDescription = null,
                            tint = ChurchGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TODAY'S PRAYER",
                            style = AppleTypographyStyles.referenceTag,
                            color = ChurchGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentVerse.prayer,
                        style = AppleTypographyStyles.devotionalProse(13.5f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. TRANSLATIONS TOGGLE & TYPOGRAPHY ADJUSTER
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Multi-translation segmented chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    translations.forEach { trans ->
                        val isSelected = translation == trans
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) RoyalNavy else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) RoyalNavy else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier
                                .clickable { viewModel.setTranslation(trans) }
                                .testTag("scripture_reader_translation_$trans")
                        ) {
                            Text(
                                text = trans,
                                style = AppleTypographyStyles.referenceTag,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.5.sp,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Typography Zoomer (A- and A+)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Font decrease
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable {
                                localFontSizeSp = (localFontSizeSp - 1.5f).coerceAtLeast(14f)
                                viewModel.setReaderFontSize(localFontSizeSp)
                            }
                            .testTag("scripture_reader_font_down_btn")
                    ) {
                        Text(
                            text = "A-",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                        )
                    }

                    // Font increase
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable {
                                localFontSizeSp = (localFontSizeSp + 1.5f).coerceAtMost(24f)
                                viewModel.setReaderFontSize(localFontSizeSp)
                            }
                            .testTag("scripture_reader_font_up_btn")
                    ) {
                        Text(
                            text = "A+",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 6. ACTION TOOLBAR: Read Full Chapter & Bookmark Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Primary Action: Read Chapter in Bible Reader
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = RoyalNavy,
                    modifier = Modifier
                        .clickable {
                            onNavigateToChapter(currentVerse.book, currentVerse.chapter, currentVerse.verse)
                        }
                        .testTag("scripture_reader_read_chapter_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Read Chapter",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = ChurchGold,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Toolbar Icons: Reflection, Audio Recitation, Bookmark, Note, Share
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Reflection Toggle Button
                    IconButton(
                        onClick = { isReflectionExpanded = !isReflectionExpanded },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("scripture_reader_reflection_toggle")
                    ) {
                        Icon(
                            imageVector = if (isReflectionExpanded) CupertinoIcons.HeartFill else CupertinoIcons.Heart,
                            contentDescription = "Toggle Ponder & Prayer",
                            tint = if (isReflectionExpanded) DevotionAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Audio Recitation
                    IconButton(
                        onClick = {
                            viewModel.toggleDailyVerseRecitation(currentVerse)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("scripture_reader_audio_btn")
                    ) {
                        Icon(
                            imageVector = if (uiState.isDailyVerseReciting) Icons.Default.StopCircle else Icons.Default.VolumeUp,
                            contentDescription = "Recite Scripture",
                            tint = if (uiState.isDailyVerseReciting) ChurchGold else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // 1-Tap Bookmark Toggle Button (Room DB)
                    IconButton(
                        onClick = {
                            viewModel.toggleDailyVerseBookmark(currentVerse)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("scripture_reader_bookmark_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isCurrentVerseBookmarked) CupertinoIcons.BookmarkFill else CupertinoIcons.Bookmark,
                            contentDescription = "Bookmark Daily Verse",
                            tint = if (isCurrentVerseBookmarked) ScriptureAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Checkmark Toggle Button in Toolbar
                    IconButton(
                        onClick = {
                            viewModel.toggleVerseReadingCompleted(currentVerse.reference)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("scripture_reader_toolbar_check_btn")
                    ) {
                        Icon(
                            imageVector = if (isCurrentVerseRead) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                            contentDescription = if (isCurrentVerseRead) "Completed Reading" else "Mark as Read",
                            tint = if (isCurrentVerseRead) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Bookmark with Reflection Note Button
                    IconButton(
                        onClick = {
                            noteInputText = existingBookmark?.note ?: ""
                            showNoteDialog = true
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("scripture_reader_add_note_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.NoteAdd,
                            contentDescription = "Attach Note to Bookmark",
                            tint = if (existingBookmark != null && existingBookmark.note.isNotBlank()) ChurchGold else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Share Button
                    IconButton(
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "“$verseText”\n— ${currentVerse.reference} ($translation)\n\nShared via Church App"
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Scripture"))
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("scripture_reader_share_btn")
                    ) {
                        Icon(
                            imageVector = CupertinoIcons.Share,
                            contentDescription = "Share Daily Verse",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
        }
    }

    // 7. BOOKMARK NOTE DIALOG
    if (showNoteDialog) {
        AlertDialog(
            onDismissRequest = { showNoteDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = null,
                        tint = ChurchGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Bookmark Reflection Note", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${currentVerse.reference} ($translation)",
                        style = AppleTypographyStyles.referenceTag,
                        color = ChurchGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "“$verseText”",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = noteInputText,
                        onValueChange = { noteInputText = it },
                        placeholder = { Text("Add your personal prayer, insight, or application...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("bookmark_note_input_field"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveBookmarkWithNote(currentVerse, noteInputText.trim())
                        showNoteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("bookmark_save_note_confirm_btn")
                ) {
                    Text("Save Note")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 8. SAVED BOOKMARKS BOTTOM SHEET
    if (showBookmarksSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBookmarksSheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.testTag("scripture_bookmarks_sheet")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(ScriptureAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = CupertinoIcons.BookmarkFill,
                                contentDescription = null,
                                tint = ScriptureAccent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SAVED SCRIPTURE BOOKMARKS",
                                style = AppleTypographyStyles.referenceTag,
                                fontWeight = FontWeight.Bold,
                                color = ScriptureAccent,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${bookmarks.size} Verses in Library",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = { showBookmarksSheet = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (bookmarks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = CupertinoIcons.Bookmark,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No Bookmarks Saved Yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap the bookmark ribbon icon on any daily verse or Bible chapter to save meaningful scriptures here.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(bookmarks) { b ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showBookmarksSheet = false
                                        onNavigateToChapter(b.book, b.chapter, b.verse)
                                    }
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${b.book} ${b.chapter}:${b.verse} (${b.translation})",
                                            style = AppleTypographyStyles.referenceTag,
                                            fontWeight = FontWeight.Bold,
                                            color = ChurchGold,
                                            fontSize = 12.sp
                                        )

                                        IconButton(
                                            onClick = {
                                                viewModel.deleteBookmark(b.book, b.chapter, b.verse)
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Remove Bookmark",
                                                tint = MaterialTheme.colorScheme.outline,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "“${b.text}”",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 20.sp
                                    )

                                    if (b.note.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = ChurchGold.copy(alpha = 0.1f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.EditNote,
                                                    contentDescription = null,
                                                    tint = ChurchGold,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = b.note,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    fontStyle = FontStyle.Italic
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Open Chapter in Bible",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = RoyalNavy,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = RoyalNavy,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 9. GOAL CELEBRATION DIALOG
    if (uiState.isCelebrationDialogOpen || showManualCelebrationDialog) {
        ReadingGoalCelebrationDialog(
            completedCount = completedCount,
            goalTarget = goalTarget,
            streakDays = uiState.readingStreakDays,
            onDismiss = {
                viewModel.dismissCelebrationDialog()
                showManualCelebrationDialog = false
            },
            onShare = {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "🎉 I just completed my daily scripture reading goal ($completedCount/$goalTarget passages)!\n" +
                        "🔥 ${uiState.readingStreakDays}-Day Scripture Reading Streak on the Church App.\n\n" +
                        "“Your word is a lamp for my feet, a light on my path.” — Psalm 119:105"
                    )
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share Scripture Goal Milestone"))
            }
        )
    }

    // 10. GOAL SETTINGS DIALOG
    if (showGoalSettingsDialog) {
        DailyGoalSettingsDialog(
            currentTarget = goalTarget,
            onSelectTarget = { target ->
                viewModel.setDailyReadingGoalTarget(target)
                showGoalSettingsDialog = false
            },
            onResetGoals = {
                viewModel.resetDailyReadingGoals()
                showGoalSettingsDialog = false
            },
            onDismiss = { showGoalSettingsDialog = false }
        )
    }
}

/**
 * Visual Reading Goal Progress Bar & Checkmark Tracker Section.
 * Shows animated linear progress bar, streak count, completion percentage, and step checkmarks.
 */
@Composable
fun DailyReadingGoalTrackerSection(
    completedCount: Int,
    goalTarget: Int,
    progressFraction: Float,
    streakDays: Int,
    isGoalReached: Boolean,
    onOpenGoalSettings: () -> Unit,
    onCelebrate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(
            1.dp,
            if (isGoalReached) ChurchGold.copy(alpha = 0.55f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Streak Badge & Goal Settings Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Streak Badge Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PastorAccent.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, PastorAccent.copy(alpha = 0.35f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "🔥", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$streakDays-Day Streak",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.5.sp
                        )
                    }
                }

                // Goal Target Pill (clickable)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOpenGoalSettings() }
                        .testTag("reading_goal_settings_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Set Daily Goal",
                            tint = RoyalNavy,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Goal: $goalTarget / day",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Daily Reading: $completedCount of $goalTarget completed",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isGoalReached) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.5.sp
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isGoalReached) Color(0xFF2E7D32).copy(alpha = 0.15f) else ChurchGold.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${(progressFraction * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isGoalReached) Color(0xFF2E7D32) else ChurchGold,
                        fontSize = 11.5.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Visual Progress Bar (Animated Gradient Bar)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressFraction.coerceIn(0.02f, 1f))
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            Brush.horizontalGradient(
                                if (isGoalReached) {
                                    listOf(ChurchGold, Color(0xFF4CAF50), Color(0xFF2E7D32))
                                } else {
                                    listOf(RoyalNavy, ChurchGold, Color(0xFF4CAF50))
                                }
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Step Checkmarks System (Milestone Badges for each passage)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (step in 1..goalTarget) {
                    val isStepComplete = step <= completedCount
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isStepComplete) Color(0xFF2E7D32).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            1.dp,
                            if (isStepComplete) Color(0xFF2E7D32).copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (isStepComplete) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isStepComplete) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Passage $step",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isStepComplete) FontWeight.Bold else FontWeight.Normal,
                                color = if (isStepComplete) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.5.sp
                            )
                        }
                    }
                }
            }

            // In-line Celebration Banner when Daily Goal is Reached
            if (isGoalReached) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ChurchGold.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, ChurchGold.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCelebrate() }
                        .testTag("reading_goal_celebrate_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "🌟", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Goal Completed! Glory to God!",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "All $goalTarget passages completed today.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ChurchGold
                        ) {
                            Text(
                                text = "Celebrate 🎉",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = RoyalNavy,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Celebratory Modal Dialog celebrating a completed scripture reading goal.
 */
@Composable
fun ReadingGoalCelebrationDialog(
    completedCount: Int,
    goalTarget: Int,
    streakDays: Int,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(ChurchGold.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Goal Trophy",
                        tint = ChurchGold,
                        modifier = Modifier.size(34.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Reading Goal Achieved!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Praise the Lord! You completed your scripture reading goal for today ($completedCount of $goalTarget passages).",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Streak Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PastorAccent.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, PastorAccent.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🔥", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$streakDays-Day Scripture Streak Active!",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Encouraging Scripture
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "“Your word is a lamp for my feet, a light on my path.”",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "— Psalm 119:105",
                            style = MaterialTheme.typography.labelSmall,
                            color = ChurchGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onShare,
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("celebration_share_btn")
            ) {
                Icon(
                    imageVector = CupertinoIcons.Share,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share Milestone")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("celebration_dismiss_btn")
            ) {
                Text("Amen")
            }
        }
    )
}

/**
 * Dialog to adjust daily reading goals or reset progress.
 */
@Composable
fun DailyGoalSettingsDialog(
    currentTarget: Int,
    onSelectTarget: (Int) -> Unit,
    onResetGoals: () -> Unit,
    onDismiss: () -> Unit
) {
    val goalOptions = listOf(
        Pair(1, "1 Scripture / Day (Light)"),
        Pair(2, "2 Scriptures / Day (Gentle)"),
        Pair(3, "3 Scriptures / Day (Standard)"),
        Pair(5, "5 Scriptures / Day (Devoted)"),
        Pair(7, "7 Scriptures / Day (Deep Study)")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = RoyalNavy,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Daily Reading Goal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select how many scriptures you aim to read and meditate on each day:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                goalOptions.forEach { (target, label) ->
                    val isSelected = currentTarget == target
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) RoyalNavy.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) RoyalNavy else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectTarget(target) }
                            .testTag("goal_option_$target")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) RoyalNavy else MaterialTheme.colorScheme.onSurface
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = RoyalNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Reset reading goals button
                OutlinedButton(
                    onClick = onResetGoals,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset Today's Reading Progress")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}
