package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.JournalEntryEntity
import com.example.data.model.Devotional
import com.example.data.repository.ChurchDataSeed
import com.example.ui.ChurchTab
import com.example.ui.ChurchViewModel
import com.example.ui.components.CupertinoIcons
import com.example.ui.components.IosGroupedCard
import com.example.ui.components.IosTopBar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevotionScreen(
    viewModel: ChurchViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val journals by viewModel.journals.collectAsState()
    val favoriteDevotionIds by viewModel.favoriteDevotionIds.collectAsState()
    val currentDevotion = uiState.selectedDevotional
    val existingJournal = uiState.currentDevotionJournal
    val isCurrentFav = favoriteDevotionIds.contains(currentDevotion.id)

    var personalReflection by remember(currentDevotion.id, existingJournal) {
        mutableStateOf(existingJournal?.reflectionText ?: "")
    }
    var personalPrayer by remember(currentDevotion.id, existingJournal) {
        mutableStateOf(existingJournal?.prayerText ?: "")
    }

    var showOnlyFavorites by remember { mutableStateOf(false) }

    val displayedDevotions = remember(showOnlyFavorites, favoriteDevotionIds) {
        if (showOnlyFavorites) {
            ChurchDataSeed.devotionals.filter { favoriteDevotionIds.contains(it.id) }
        } else {
            ChurchDataSeed.devotionals
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // iOS Top Bar with Cupertino Icons
        IosTopBar(
            title = "Daily Devotions",
            subtitle = "SPIRITUAL NOURISHMENT",
            actions = {
                // Profile View Switcher
                IconButton(
                    onClick = { viewModel.selectTab(ChurchTab.PROFILE) },
                    modifier = Modifier.testTag("devotion_open_profile_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(RoyalNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "EM",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChurchGold
                        )
                    }
                }
                // Favorite Toggle for current devotion
                IconButton(
                    onClick = { viewModel.toggleFavoriteDevotion(currentDevotion.id) },
                    modifier = Modifier.testTag("devotion_toggle_favorite_top_button")
                ) {
                    Icon(
                        imageVector = if (isCurrentFav) CupertinoIcons.HeartFill else CupertinoIcons.Heart,
                        contentDescription = "Favorite Devotion",
                        tint = if (isCurrentFav) ChurchGold else RoyalNavy
                    )
                }
                // Open Journal Tab
                IconButton(
                    onClick = { viewModel.selectTab(ChurchTab.JOURNAL) },
                    modifier = Modifier.testTag("devotion_open_journal_tab_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (journals.isNotEmpty()) {
                                Badge { Text(journals.size.toString()) }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = CupertinoIcons.SquareAndPencil,
                            contentDescription = "My Spiritual Journal",
                            tint = RoyalNavy
                        )
                    }
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Devotional Streak & Favorites Summary Banner
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DevotionAccent.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DevotionAccent.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(DevotionAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = CupertinoIcons.FlameFill,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${uiState.devotionStreakDays}-Day Devotion Streak",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DevotionAccent
                                )
                                Text(
                                    text = "Faithful daily walk in the Word",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = CupertinoIcons.HeartFill,
                                    contentDescription = null,
                                    tint = ChurchGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${favoriteDevotionIds.size} Saved",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ChurchGold
                                )
                            }
                        }
                    }
                }
            }

            // 2. Filter Tabs: All Devotions vs Favorites
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        onClick = { showOnlyFavorites = false },
                        shape = RoundedCornerShape(12.dp),
                        color = if (!showOnlyFavorites) RoyalNavy else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (!showOnlyFavorites) RoyalNavy else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("filter_all_devotions_button")
                    ) {
                        Text(
                            text = "All Devotions (${ChurchDataSeed.devotionals.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (!showOnlyFavorites) FontWeight.Bold else FontWeight.Medium,
                            color = if (!showOnlyFavorites) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    Surface(
                        onClick = { showOnlyFavorites = true },
                        shape = RoundedCornerShape(12.dp),
                        color = if (showOnlyFavorites) ChurchGold else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (showOnlyFavorites) ChurchGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("filter_favorite_devotions_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = CupertinoIcons.HeartFill,
                                contentDescription = null,
                                tint = if (showOnlyFavorites) Color.White else ChurchGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Favorites (${favoriteDevotionIds.size})",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (showOnlyFavorites) FontWeight.Bold else FontWeight.Medium,
                                color = if (showOnlyFavorites) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // 3. Devotion Picker Chips
            if (displayedDevotions.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = CupertinoIcons.Heart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No Favorited Devotions Yet",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Tap the heart icon on any devotional to keep your favorite meditations handy.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(displayedDevotions) { dev ->
                            val isSelected = dev.id == currentDevotion.id
                            val isFav = favoriteDevotionIds.contains(dev.id)
                            Surface(
                                onClick = { viewModel.selectDevotional(dev) },
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) RoyalNavy else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) RoyalNavy else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.testTag("devotion_date_chip_${dev.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = dev.date,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (isFav) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = CupertinoIcons.HeartFill,
                                                contentDescription = "Favorited",
                                                tint = if (isSelected) ChurchGoldLight else ChurchGold,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = dev.scriptureRef,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected) ChurchGoldLight else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Devotion Content Card with Favorite Action
            item {
                IosGroupedCard(
                    modifier = Modifier.testTag("devotion_main_card")
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MEDITATION OF THE DAY",
                                style = MaterialTheme.typography.labelSmall,
                                color = ChurchGold,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${currentDevotion.readingTimeMinutes} min read",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { viewModel.toggleFavoriteDevotion(currentDevotion.id) },
                                    modifier = Modifier.size(32.dp).testTag("devotion_card_fav_button")
                                ) {
                                    Icon(
                                        imageVector = if (isCurrentFav) CupertinoIcons.HeartFill else CupertinoIcons.Heart,
                                        contentDescription = "Favorite",
                                        tint = if (isCurrentFav) ChurchGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = currentDevotion.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Author: ${currentDevotion.authorPastor}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Scripture Reading Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ScriptureAccent.copy(alpha = 0.08f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ScriptureAccent.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = CupertinoIcons.Book,
                                        contentDescription = null,
                                        tint = ScriptureAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = currentDevotion.scriptureRef,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = ScriptureAccent
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "\"${currentDevotion.scriptureText}\"",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontFamily = FontFamily.Serif,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 24.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Pastoral Reflection Text
                        Text(
                            text = "Pastoral Reflection",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentDevotion.reflectionText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 26.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Guided Prayer Section
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DevotionAccent.copy(alpha = 0.08f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DevotionAccent.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = CupertinoIcons.HandsSparkles,
                                        contentDescription = null,
                                        tint = DevotionAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Guided Prayer",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = DevotionAccent
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = currentDevotion.guidedPrayer,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = FontFamily.Serif,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 22.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Discussion Question
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = CupertinoIcons.Quote,
                                    contentDescription = null,
                                    tint = ChurchGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Question to Ponder:",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ChurchGold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = currentDevotion.discussionQuestion,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Personal Devotional Journal Quick Box
            item {
                IosGroupedCard(
                    modifier = Modifier.testTag("devotion_journal_editor_card")
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = CupertinoIcons.SquareAndPencil,
                                    contentDescription = null,
                                    tint = RoyalNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Journal Your Reflections",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            if (existingJournal != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PrayerAccent.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "Saved",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PrayerAccent,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = personalReflection,
                            onValueChange = { personalReflection = it },
                            label = { Text("What is God speaking to you today?") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalNavy
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = personalPrayer,
                            onValueChange = { personalPrayer = it },
                            label = { Text("Write your personal prayer...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DevotionAccent
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.saveJournalEntry(
                                        devotionId = currentDevotion.id,
                                        reflection = personalReflection,
                                        prayer = personalPrayer
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("save_devotion_journal_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
                            ) {
                                Icon(imageVector = CupertinoIcons.DocText, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (existingJournal != null) "Update Journal" else "Save Reflection",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = { viewModel.selectTab(ChurchTab.JOURNAL) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("goto_journal_tab_button")
                            ) {
                                Text("View All Notes")
                            }
                        }
                    }
                }
            }
        }
    }
}
