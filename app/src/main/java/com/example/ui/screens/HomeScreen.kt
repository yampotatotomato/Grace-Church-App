package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrayerGroup
import com.example.data.repository.ChurchDataSeed
import com.example.ui.ChurchTab
import com.example.ui.ChurchViewModel
import com.example.ui.components.CupertinoIcons
import com.example.ui.components.DailyScriptureCard
import com.example.ui.components.IosGroupedCard
import com.example.ui.components.IosTopBar
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: ChurchViewModel,
    onNavigateTab: (ChurchTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val joinedGroups by viewModel.joinedGroups.collectAsState()
    val favoriteDevotionIds by viewModel.favoriteDevotionIds.collectAsState()
    val journals by viewModel.journals.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val publishedAnnouncements by viewModel.publishedAnnouncements.collectAsState()
    val dailyVerse = remember { ChurchDataSeed.dailyVerse }
    val isDailyVerseBookmarked = bookmarks.any { it.book == "Romans" && it.chapter == 8 && it.verse == 38 }
    val latestSermon = remember { ChurchDataSeed.sermons.first() }
    val todayDevotion = remember { ChurchDataSeed.devotionals.first() }
    val featuredGroup = remember { ChurchDataSeed.prayerGroups.first() }
    val isTodayDevotionFav = favoriteDevotionIds.contains(todayDevotion.id)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top iOS Bar with Cupertino Icons
        IosTopBar(
            title = "Grace Church",
            subtitle = "Daily Sanctuary",
            actions = {
                IconButton(
                    onClick = { onNavigateTab(ChurchTab.PROFILE) },
                    modifier = Modifier.testTag("home_profile_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(RoyalNavy)
                            .border(1.dp, uiState.accentTheme.accentColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "EM",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChurchGold
                        )
                    }
                }
                IconButton(
                    onClick = { onNavigateTab(ChurchTab.JOURNAL) },
                    modifier = Modifier.testTag("home_journal_button")
                ) {
                    Icon(
                        imageVector = CupertinoIcons.SquareAndPencil,
                        contentDescription = "Spiritual Journal",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = { viewModel.openNotificationSettings() },
                    modifier = Modifier.testTag("home_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Theme & Customization",
                        tint = uiState.accentTheme.accentColor
                    )
                }
                IconButton(
                    onClick = { viewModel.openNotificationSettings() },
                    modifier = Modifier.testTag("home_notifications_button")
                ) {
                    Icon(
                        imageVector = CupertinoIcons.Bell,
                        contentDescription = "Notifications & Settings",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Clean & Minimal Scripture Reading Component (Daily Verse)
            item {
                DailyScriptureCard(
                    dailyVerse = dailyVerse,
                    translation = uiState.selectedTranslation,
                    isBookmarked = isDailyVerseBookmarked,
                    onReadChapter = {
                        viewModel.selectTab(ChurchTab.SCRIPTURE)
                        val romans = ChurchDataSeed.bibleBooks.find { it.name == "Romans" }
                        if (romans != null) {
                            viewModel.setScriptureBook(romans, 8)
                        }
                    },
                    onToggleBookmark = {
                        viewModel.toggleBookmark("Romans", 8, 38, dailyVerse.text)
                    },
                    onListen = {
                        viewModel.showToast("Reciting ${dailyVerse.reference}...")
                    },
                    modifier = Modifier.testTag("home_daily_scripture_component")
                )
            }

            // 2. Quick Navigation Grid with Cupertino Icons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickNavButton(
                        title = "Scripture",
                        subtitle = "Holy Bible",
                        icon = CupertinoIcons.Book,
                        accentColor = ScriptureAccent,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(ChurchTab.SCRIPTURE) }
                    )
                    QuickNavButton(
                        title = "Devotions",
                        subtitle = "${favoriteDevotionIds.size} Saved",
                        icon = CupertinoIcons.Heart,
                        accentColor = DevotionAccent,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(ChurchTab.DEVOTION) }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickNavButton(
                        title = "Spiritual Journal",
                        subtitle = "${journals.size} Reflections",
                        icon = CupertinoIcons.SquareAndPencil,
                        accentColor = RoyalNavy,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(ChurchTab.JOURNAL) }
                    )
                    QuickNavButton(
                        title = "Prayer Groups",
                        subtitle = "Find Fellowship",
                        icon = CupertinoIcons.Person2,
                        accentColor = PrayerAccent,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(ChurchTab.COMMUNITY) }
                    )
                }
            }

            // 3. Pastoral Letters & Sanctuary Announcements
            item {
                Column {
                    SectionHeader(
                        title = "Sanctuary Bulletins & Letters",
                        actionText = "Staff Portal",
                        onAction = { onNavigateTab(ChurchTab.COMPANION) }
                    )

                    if (publishedAnnouncements.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            publishedAnnouncements.take(3).forEach { announcement ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { viewModel.openAnnouncementDetail(announcement) }
                                        .testTag("home_announcement_item_${announcement.id}"),
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        if (announcement.isPinned) ChurchGold.copy(alpha = 0.5f)
                                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                    ),
                                    shadowElevation = 2.dp
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Surface(
                                                    color = MaterialTheme.colorScheme.primaryContainer,
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text(
                                                        text = announcement.category,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }

                                                if (announcement.isPinned) {
                                                    Surface(
                                                        color = ChurchGold.copy(alpha = 0.2f),
                                                        shape = RoundedCornerShape(6.dp)
                                                    ) {
                                                        Text(
                                                            text = "📌 Pinned",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = ChurchGoldDark,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            Text(
                                                text = announcement.authorPastorName,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = announcement.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = announcement.content,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            lineHeight = 18.sp
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (announcement.scriptureRef.isNotBlank()) {
                                                Text(
                                                    text = "📖 ${announcement.scriptureRef}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            } else {
                                                Spacer(modifier = Modifier.width(1.dp))
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "Read Letter",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Icon(
                                                    imageVector = CupertinoIcons.ChevronRight,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
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

            // 4. Today's Devotional Highlight
            item {
                Column {
                    SectionHeader(
                        title = "Today's Devotion",
                        actionText = "All Devotions",
                        onAction = { onNavigateTab(ChurchTab.DEVOTION) }
                    )

                    IosGroupedCard(
                        onClick = {
                            viewModel.selectDevotional(todayDevotion)
                            onNavigateTab(ChurchTab.DEVOTION)
                        },
                        modifier = Modifier.testTag("home_today_devotion_card")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DevotionAccent.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = CupertinoIcons.HeartFill,
                                    contentDescription = null,
                                    tint = DevotionAccent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = todayDevotion.scriptureRef,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DevotionAccent,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = todayDevotion.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "By ${todayDevotion.authorPastor} • ${todayDevotion.readingTimeMinutes} min read",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = { viewModel.toggleFavoriteDevotion(todayDevotion.id) },
                                modifier = Modifier.size(32.dp).testTag("home_fav_devotion_button")
                            ) {
                                Icon(
                                    imageVector = if (isTodayDevotionFav) CupertinoIcons.HeartFill else CupertinoIcons.Heart,
                                    contentDescription = "Favorite",
                                    tint = if (isTodayDevotionFav) ChurchGold else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Latest Pastoral Teaching
            item {
                Column {
                    SectionHeader(
                        title = "Latest Sermon",
                        actionText = "Pastors & Media",
                        onAction = { onNavigateTab(ChurchTab.SERMONS) }
                    )

                    IosGroupedCard(
                        onClick = {
                            viewModel.playSermon(latestSermon)
                            onNavigateTab(ChurchTab.SERMONS)
                        },
                        modifier = Modifier.testTag("home_latest_sermon_card")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(PastorAccent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (uiState.isAudioPlaying && uiState.activeSermon?.id == latestSermon.id) {
                                        CupertinoIcons.PauseFill
                                    } else {
                                        CupertinoIcons.PlayFill
                                    },
                                    contentDescription = "Play Sermon",
                                    tint = PastorAccent,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = latestSermon.seriesName.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PastorAccent,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = latestSermon.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${latestSermon.pastorName} • ${latestSermon.durationMinutes} min",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 5. Featured Community Prayer Group
            item {
                Column {
                    SectionHeader(
                        title = "Local Prayer Groups",
                        actionText = "Find in Area",
                        onAction = { onNavigateTab(ChurchTab.COMMUNITY) }
                    )

                    val isJoined = joinedGroups.contains(featuredGroup.id)

                    IosGroupedCard(
                        onClick = { onNavigateTab(ChurchTab.COMMUNITY) },
                        modifier = Modifier.testTag("home_featured_group_card")
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = featuredGroup.area.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrayerAccent,
                                    fontWeight = FontWeight.Bold
                                )
                                if (isJoined) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = PrayerAccent.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "Joined",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = PrayerAccent,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = featuredGroup.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = CupertinoIcons.Calendar,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = featuredGroup.meetingDayTime,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = CupertinoIcons.Location,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = featuredGroup.locationName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        viewModel.triggerTestMeetingPush(context, featuredGroup)
                                    },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Icon(
                                        imageVector = CupertinoIcons.Bell,
                                        contentDescription = null,
                                        tint = IosBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Send Meeting Reminder",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = IosBlue,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Button(
                                    onClick = { viewModel.toggleJoinGroup(featuredGroup.id) },
                                    shape = RoundedCornerShape(18.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isJoined) MaterialTheme.colorScheme.surfaceVariant else RoyalNavy,
                                        contentColor = if (isJoined) MaterialTheme.colorScheme.onSurface else Color.White
                                    ),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (isJoined) "Leave" else "RSVP / Join",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (uiState.selectedAnnouncementForDetail != null) {
            AnnouncementDetailModal(
                announcement = uiState.selectedAnnouncementForDetail!!,
                onDismiss = { viewModel.closeAnnouncementDetail() },
                onNavigateTab = onNavigateTab
            )
        }
    }
}

@Composable
fun QuickNavButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag("nav_btn_${title.lowercase().replace(" ", "_")}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        TextButton(
            onClick = onAction,
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelLarge,
                color = IosBlue,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
