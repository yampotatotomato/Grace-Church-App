package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ChurchDataSeed
import com.example.ui.ChurchViewModel
import com.example.ui.components.CupertinoIcons
import com.example.ui.components.IosGroupedCard
import com.example.ui.components.IosSegmentedControl
import com.example.ui.components.IosTopBar
import com.example.ui.theme.*

@Composable
fun SettingsNotificationsScreen(
    viewModel: ChurchViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var selectedSettingsSection by remember { mutableIntStateOf(0) }
    val sectionTabs = listOf("Appearance & Color", "Apple Typography", "Alerts & Push")

    val timeOptions = listOf("06:00 AM", "07:00 AM", "08:00 AM", "09:00 AM", "10:00 AM")
    val translations = listOf("NIV", "ESV", "KJV", "NLT")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        IosTopBar(
            title = "Settings & Theme",
            subtitle = "CUSTOMIZATION & PREFERENCES",
            titleIcon = Icons.Default.Tune,
            navigationIcon = Icons.Default.Close,
            onNavigationClick = onBack
        )

        // Section Tabs
        IosSegmentedControl(
            items = sectionTabs,
            selectedIndex = selectedSettingsSection,
            onSelectedIndexChanged = { selectedSettingsSection = it },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedSettingsSection) {
                0 -> {
                    // SECTION 0: APPEARANCE & COLOR THEMES
                    item {
                        // 1. Live Interactive Theme Preview Card
                        LiveThemePreviewCard(
                            themeMode = uiState.themeMode,
                            accentTheme = uiState.accentTheme,
                            fontPreset = uiState.fontPreset,
                            fontSizeSp = uiState.readerFontSizeSp
                        )
                    }

                    // 2. Theme Mode (System, Light, Dark)
                    item {
                        Text(
                            text = "APPEARANCE MODE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemeMode.values().forEach { mode ->
                                val isSelected = uiState.themeMode == mode
                                val containerColor by animateColorAsState(
                                    targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                    animationSpec = tween(200),
                                    label = "theme_mode_bg"
                                )
                                val borderColor by animateColorAsState(
                                    targetValue = if (isSelected) uiState.accentTheme.accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    animationSpec = tween(200),
                                    label = "theme_mode_border"
                                )

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = containerColor,
                                    border = androidx.compose.foundation.BorderStroke(
                                        if (isSelected) 2.dp else 1.dp,
                                        borderColor
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setThemeMode(mode) }
                                        .testTag("theme_mode_${mode.name.lowercase()}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = when (mode) {
                                                ThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                                                ThemeMode.LIGHT -> Icons.Default.LightMode
                                                ThemeMode.DARK -> Icons.Default.DarkMode
                                            },
                                            contentDescription = null,
                                            tint = if (isSelected) uiState.accentTheme.accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = mode.title,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. Color Palette Selection
                    item {
                        Text(
                            text = "COLOR PALETTE & ACCENTS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Customize the sanctuary highlights, interactive accents, and background gradients.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(AccentTheme.values().toList()) { palette ->
                        val isSelected = uiState.accentTheme == palette
                        val borderColor by animateColorAsState(
                            targetValue = if (isSelected) palette.accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                            animationSpec = tween(200),
                            label = "palette_border"
                        )
                        val containerColor by animateColorAsState(
                            targetValue = if (isSelected) palette.accentColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                            animationSpec = tween(200),
                            label = "palette_bg"
                        )

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = containerColor,
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                borderColor
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setAccentTheme(palette) }
                                .testTag("palette_choice_${palette.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Color swatches bubble
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(palette.primaryColor)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(palette.accentColor)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(palette.secondaryContainerLight)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = palette.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) palette.accentColor else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = palette.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp
                                    )
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(palette.accentColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // SECTION 1: APPLE APPROVED 3-FONT MIXER
                    item {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(uiState.accentTheme.accentColor.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FormatQuote,
                                            contentDescription = null,
                                            tint = uiState.accentTheme.accentColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Apple 3-Font Harmonious System",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Crafted according to Apple Human Interface Guidelines",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Explanations for the 3 Apple fonts
                                FontTierCard(
                                    tierNumber = "1",
                                    fontName = "SF Pro (San Francisco)",
                                    role = "UI Controls, Navigation, Buttons & Labels",
                                    sampleText = "The clean, grotesque Apple sans-serif providing high legibility across all screen sizes.",
                                    fontFamily = AppleFontFamilies.SfProSans,
                                    badgeColor = uiState.accentTheme.accentColor
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                FontTierCard(
                                    tierNumber = "2",
                                    fontName = "New York (Transitional Serif)",
                                    role = "Sacred Scripture, Devotionals & Sermon Titles",
                                    sampleText = "“In the beginning was the Word, and the Word was with God, and the Word was God.” — John 1:1",
                                    fontFamily = AppleFontFamilies.NewYorkSerif,
                                    badgeColor = ChurchGold
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                FontTierCard(
                                    tierNumber = "3",
                                    fontName = "SF Mono (Monospaced)",
                                    role = "Verse Numbers, Concordances, Audio Timers & Tags",
                                    sampleText = "[ROM 8:28]  07:00 AM  •  28:15 MIN  •  VERSE #16",
                                    fontFamily = AppleFontFamilies.SfMono,
                                    badgeColor = DevotionAccent
                                )
                            }
                        }
                    }

                    // Font Presets Selection
                    item {
                        Text(
                            text = "TYPOGRAPHY PAIRING PRESET",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            FontPreset.values().forEach { preset ->
                                val isSelected = uiState.fontPreset == preset
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) uiState.accentTheme.accentColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) uiState.accentTheme.accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.setFontPreset(preset) }
                                        .testTag("font_preset_${preset.name.lowercase()}")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { viewModel.setFontPreset(preset) },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = uiState.accentTheme.accentColor
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = preset.title,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = preset.subtitle,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Reader Font Size Slider
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Scripture Reading Font Size",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${uiState.readerFontSizeSp.toInt()} sp",
                                        style = AppleTypographyStyles.referenceTag,
                                        color = uiState.accentTheme.accentColor
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Slider(
                                    value = uiState.readerFontSizeSp,
                                    onValueChange = { viewModel.setReaderFontSize(it) },
                                    valueRange = 14f..26f,
                                    steps = 5,
                                    colors = SliderDefaults.colors(
                                        thumbColor = uiState.accentTheme.accentColor,
                                        activeTrackColor = uiState.accentTheme.accentColor
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Live text scale preview in New York Serif
                                Text(
                                    text = "“Your word is a lamp for my feet, a light on my path.” (Psalm 119:105)",
                                    style = AppleTypographyStyles.scriptureText(uiState.readerFontSizeSp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                2 -> {
                    // SECTION 2: ALERTS, PUSH & PREFERENCES
                    item {
                        Text(
                            text = "PUSH NOTIFICATIONS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        IosGroupedCard(
                            modifier = Modifier.testTag("notification_settings_card")
                        ) {
                            // Daily Verse Push Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(uiState.accentTheme.accentColor.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = uiState.accentTheme.accentColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Daily Verse Push Alerts",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Scripture delivered every morning",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Switch(
                                    checked = uiState.dailyVerseNotificationEnabled,
                                    onCheckedChange = { viewModel.toggleDailyVerseNotifications(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = uiState.accentTheme.accentColor
                                    ),
                                    modifier = Modifier.testTag("toggle_daily_verse_notifications")
                                )
                            }

                            if (uiState.dailyVerseNotificationEnabled) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Daily Delivery Time:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    timeOptions.forEach { time ->
                                        val isSelected = uiState.dailyVerseTime == time
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { viewModel.setDailyVerseTime(time) },
                                            label = {
                                                Text(
                                                    text = time,
                                                    style = AppleTypographyStyles.referenceTag
                                                )
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = uiState.accentTheme.accentColor.copy(alpha = 0.2f),
                                                selectedLabelColor = uiState.accentTheme.accentColor
                                            )
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 14.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )

                            // Meeting Reminders Push Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(DevotionAccent.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Event,
                                            contentDescription = null,
                                            tint = DevotionAccent,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Fellowship Schedule Alerts",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Reminders 1 hr prior to prayer meetings",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Switch(
                                    checked = uiState.meetingReminderEnabled,
                                    onCheckedChange = { viewModel.toggleMeetingReminders(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = uiState.accentTheme.accentColor
                                    ),
                                    modifier = Modifier.testTag("toggle_meeting_reminders")
                                )
                            }
                        }
                    }

                    // Test Push Alerts Button
                    item {
                        Text(
                            text = "SIMULATE / TEST NOTIFICATIONS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.triggerTestDailyVersePush(context) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Test Verse", style = AppleTypographyStyles.uiButton, fontSize = 13.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.triggerTestMeetingPush(context, ChurchDataSeed.prayerGroups.first())
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationAdd,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Test Meeting", style = AppleTypographyStyles.uiButton, fontSize = 13.sp)
                            }
                        }
                    }

                    // Default Bible Translation
                    item {
                        Text(
                            text = "DEFAULT BIBLE TRANSLATION",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            translations.forEach { trans ->
                                val isSelected = uiState.selectedTranslation == trans
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) uiState.accentTheme.accentColor else MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) uiState.accentTheme.accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setTranslation(trans) }
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = trans,
                                            style = AppleTypographyStyles.referenceTag,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Multi-Step Onboarding Walkthrough Review
                    item {
                        Text(
                            text = "APP GUIDED TOUR & WALKTHROUGH",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                uiState.accentTheme.accentColor.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.openOnboardingReview()
                                }
                                .testTag("settings_replay_onboarding_button")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(uiState.accentTheme.accentColor.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Explore,
                                                contentDescription = null,
                                                tint = uiState.accentTheme.accentColor,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "Review Guided Walkthrough",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Text(
                                                text = "6-step sequence introducing Community, Scripture & Care",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = uiState.accentTheme.accentColor.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "6 STEPS",
                                            style = AppleTypographyStyles.referenceTag,
                                            color = uiState.accentTheme.accentColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        viewModel.openOnboardingReview()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = uiState.accentTheme.accentColor,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Start Feature Tour",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // App Info Footer
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Grace Church Sanctuary App",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "v2.5.0 • Apple Human Interface Design",
                                    style = AppleTypographyStyles.referenceTag,
                                    color = MaterialTheme.colorScheme.outline,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiveThemePreviewCard(
    themeMode: ThemeMode,
    accentTheme: AccentTheme,
    fontPreset: FontPreset,
    fontSizeSp: Float
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = accentTheme.primaryColor,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .testTag("live_theme_preview_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            accentTheme.primaryColor,
                            accentTheme.accentColor.copy(alpha = 0.4f)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.35f)
                    ) {
                        Text(
                            text = "LIVE THEME & FONT PREVIEW",
                            style = AppleTypographyStyles.referenceTag,
                            color = accentTheme.accentColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = themeMode.title,
                        style = AppleTypographyStyles.referenceTag,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // New York Serif Scripture Quote
                Text(
                    text = "“Blessed are the pure in heart, for they shall see God.”",
                    style = AppleTypographyStyles.scriptureText(19f),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // SF Mono Verse Reference Tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MATTHEW 5:8 • NIV",
                        style = AppleTypographyStyles.referenceTag,
                        color = accentTheme.accentColor
                    )

                    // SF Pro Button Preview
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = accentTheme.accentColor
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = CupertinoIcons.Sparkles,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = accentTheme.title.take(14),
                                style = AppleTypographyStyles.uiButton,
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FontTierCard(
    tierNumber: String,
    fontName: String,
    role: String,
    sampleText: String,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    badgeColor: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(badgeColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tierNumber,
                            style = AppleTypographyStyles.referenceTag,
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = fontName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "APPLE TYPOGRAPHY",
                        style = AppleTypographyStyles.referenceTag,
                        color = badgeColor,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Used for: $role",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = sampleText,
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
