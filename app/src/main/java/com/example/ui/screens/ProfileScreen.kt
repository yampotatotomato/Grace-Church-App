package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ChurchDataSeed
import com.example.ui.ChurchTab
import com.example.ui.ChurchViewModel
import com.example.ui.components.CupertinoIcons
import com.example.ui.components.IosGroupedCard
import com.example.ui.components.IosTopBar
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: ChurchViewModel,
    onNavigateToScripture: () -> Unit,
    onNavigateToDevotional: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateTab: (ChurchTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val favoriteDevotionIds by viewModel.favoriteDevotionIds.collectAsState()
    val journals by viewModel.journals.collectAsState()
    val joinedGroups by viewModel.joinedGroups.collectAsState()

    val translations = listOf("NIV", "ESV", "KJV", "NLT")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        IosTopBar(
            title = "Spiritual Profile",
            subtitle = "MEMBER & SANCTUARY",
            titleIcon = CupertinoIcons.PersonFill,
            actions = {
                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier.testTag("profile_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Preferences & Settings",
                        tint = uiState.accentTheme.accentColor
                    )
                }
            }
        )

        // Core View Switcher Quick Bar (Scripture <-> Devotional <-> Profile)
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Scripture Shortcut
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ScriptureAccent.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, ScriptureAccent.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToScripture() }
                        .testTag("profile_nav_to_scripture")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = CupertinoIcons.Book,
                            contentDescription = null,
                            tint = ScriptureAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Scripture",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = ScriptureAccent
                        )
                    }
                }

                // Devotional Shortcut
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DevotionAccent.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, DevotionAccent.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToDevotional() }
                        .testTag("profile_nav_to_devotional")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = CupertinoIcons.HeartFill,
                            contentDescription = null,
                            tint = DevotionAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Devotional",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = DevotionAccent
                        )
                    }
                }

                // Profile (Active)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = RoyalNavy,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("profile_nav_active")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = CupertinoIcons.PersonFill,
                            contentDescription = null,
                            tint = ChurchGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Profile",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Member Profile Header Card
            item {
                IosGroupedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar Monogram
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(RoyalNavy)
                                    .border(2.dp, uiState.accentTheme.accentColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "EM",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = ChurchGold
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Eric M.",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = ChurchGold.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "MEMBER",
                                            style = AppleTypographyStyles.referenceTag,
                                            color = RoyalNavy,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "Grace Sanctuary Community",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Preferred: ${uiState.selectedTranslation} Bible • Alert at ${uiState.dailyVerseTime}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = uiState.accentTheme.accentColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // 2. Spiritual Milestones & Stats Grid
            item {
                Text(
                    text = "FAITH MILESTONES & DISCIPLINE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Devotional Streak
                    StatBadgeCard(
                        title = "14 Days",
                        label = "Devotion Streak",
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = ChurchGold,
                        modifier = Modifier.weight(1f)
                    )

                    // Saved Scriptures
                    StatBadgeCard(
                        title = "${bookmarks.size} Verses",
                        label = "Scripture Saved",
                        icon = Icons.Default.Bookmark,
                        iconTint = ScriptureAccent,
                        modifier = Modifier.weight(1f)
                    )

                    // Journal Reflections
                    StatBadgeCard(
                        title = "${journals.size} Entries",
                        label = "Faith Journals",
                        icon = CupertinoIcons.SquareAndPencil,
                        iconTint = DevotionAccent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 3. Quick Core Views Navigation Hub
            item {
                Text(
                    text = "SANCTUARY HUBS & NAVIGATION",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Jump to Scripture View
                    NavigationCardItem(
                        icon = CupertinoIcons.Book,
                        iconColor = ScriptureAccent,
                        title = "Holy Scripture & Chapter Study",
                        subtitle = "Currently in ${uiState.selectedBook.name} Chapter ${uiState.selectedChapterNumber} (${uiState.selectedTranslation})",
                        actionLabel = "Open Scripture",
                        onClick = onNavigateToScripture,
                        testTag = "profile_hub_open_scripture"
                    )

                    // Jump to Devotional View
                    NavigationCardItem(
                        icon = CupertinoIcons.HeartFill,
                        iconColor = DevotionAccent,
                        title = "Daily Guided Devotional",
                        subtitle = "${uiState.selectedDevotional.title} • ${uiState.selectedDevotional.scriptureRef}",
                        actionLabel = "Open Devotional",
                        onClick = onNavigateToDevotional,
                        testTag = "profile_hub_open_devotional"
                    )

                    // Jump to Journal View
                    NavigationCardItem(
                        icon = CupertinoIcons.SquareAndPencil,
                        iconColor = ChurchGold,
                        title = "Spiritual Journal & Prayers",
                        subtitle = "${journals.size} private reflections recorded",
                        actionLabel = "Open Journal",
                        onClick = { onNavigateTab(ChurchTab.JOURNAL) },
                        testTag = "profile_hub_open_journal"
                    )

                    // Jump to Pastoral Counseling
                    NavigationCardItem(
                        icon = Icons.Default.Shield,
                        iconColor = PastorAccent,
                        title = "Pastoral Care & Counseling",
                        subtitle = "Confidential spiritual guidance and pastoral directory",
                        actionLabel = "Open Pastors",
                        onClick = { onNavigateTab(ChurchTab.SERMONS) },
                        testTag = "profile_hub_open_pastors"
                    )
                }
            }

            // 4. Saved Bookmarks Quick List
            if (bookmarks.isNotEmpty()) {
                item {
                    Text(
                        text = "YOUR SAVED SCRIPTURES (${bookmarks.size})",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    IosGroupedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            bookmarks.take(3).forEach { bookmark ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.setScripturePassage(
                                                bookName = bookmark.book,
                                                chapterNum = bookmark.chapter,
                                                verseNum = bookmark.verse
                                            )
                                            onNavigateToScripture()
                                        }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${bookmark.book} ${bookmark.chapter}:${bookmark.verse}",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = ScriptureAccent
                                        )
                                        Text(
                                            text = bookmark.text,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Go to verse",
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Quick Preferences & Personalization
            item {
                Text(
                    text = "BIBLE & ALERT PREFERENCES",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                IosGroupedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Translation Selector
                        Column {
                            Text(
                                text = "Active Bible Translation",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                translations.forEach { trans ->
                                    val isSelected = uiState.selectedTranslation == trans
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) RoyalNavy else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.setTranslation(trans) }
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(vertical = 8.dp),
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
                        }

                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                        // Pastor & Staff Companion Portal
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateTab(ChurchTab.COMPANION) }
                                .testTag("profile_nav_to_companion"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(ChurchGold.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = CupertinoIcons.Sparkles,
                                        contentDescription = null,
                                        tint = ChurchGoldDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Pastor & Staff Companion Portal",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (uiState.isPastorLoggedIn) "Active: ${uiState.currentPastorUser?.name}" else "Publish bulletins, schedule posts & alerts",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                        // Replay Onboarding Walkthrough
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.openOnboardingReview() },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Explore,
                                    contentDescription = null,
                                    tint = uiState.accentTheme.accentColor,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Review Guided Walkthrough",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "5-step onboarding tour introducing core features",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBadgeCard(
    title: String,
    label: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    IosGroupedCard(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun NavigationCardItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    actionLabel: String,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = actionLabel,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
