package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ChurchDataSeed
import com.example.ui.ChurchViewModel
import com.example.ui.components.IosGroupedCard
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

    val timeOptions = listOf("06:00 AM", "07:00 AM", "08:00 AM", "09:00 AM", "10:00 AM")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        IosTopBar(
            title = "Settings & Alerts",
            subtitle = "PREFERENCES & PUSH NOTIFICATIONS",
            navigationIcon = Icons.Default.Close,
            onNavigationClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 60.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Push Notifications Configuration Card
            item {
                Text(
                    text = "Push Notification System",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Stay anchored in the Word and connected with your fellowship schedule.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
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
                                    .background(ScriptureAccent.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = ScriptureAccent,
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
                                    label = { Text(time, fontSize = 11.sp) }
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
                                    .background(PrayerAccent.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = null,
                                    tint = PrayerAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Prayer Group & Service Reminders",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Alerts 1 hour before scheduled meetings",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = uiState.meetingReminderEnabled,
                            onCheckedChange = { viewModel.toggleMeetingReminders(it) },
                            modifier = Modifier.testTag("toggle_meeting_reminders")
                        )
                    }
                }
            }

            // 2. Test Notification Actions Card
            item {
                Text(
                    text = "Notification Simulator & Tests",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                IosGroupedCard {
                    Text(
                        text = "Trigger immediate local notification banners to test device lockscreen notifications.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.triggerTestDailyVersePush(context) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_trigger_daily_verse_push"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
                    ) {
                        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send Daily Verse Notification Now")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val firstGroup = ChurchDataSeed.prayerGroups.first()
                            viewModel.triggerTestMeetingPush(context, firstGroup)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_trigger_meeting_push"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrayerAccent)
                    ) {
                        Icon(imageVector = Icons.Default.Alarm, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send Meeting Reminder Notification Now")
                    }
                }
            }

            // 3. Accessibility & Display Preferences
            item {
                Text(
                    text = "Accessibility & Reading Experience",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                IosGroupedCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reader Font Scale",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${uiState.readerFontSizeSp.toInt()} sp",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = IosBlue
                        )
                    }

                    Slider(
                        value = uiState.readerFontSizeSp,
                        onValueChange = { viewModel.setReaderFontSize(it) },
                        valueRange = 14f..26f,
                        steps = 5,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Scripture and devotion text will scale comfortably according to this size.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 4. Onboarding Tour & App Info
            item {
                IosGroupedCard {
                    Text(
                        text = "Grace Church App",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Version 1.0.0 • Designed with iOS HIG aesthetics & Material 3 for Android",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            viewModel.resetToOnboarding()
                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Replay Welcome & Onboarding Tour")
                    }
                }
            }
        }
    }
}
