package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PrayerRequestEntity
import com.example.data.model.PrayerGroup
import com.example.data.repository.ChurchDataSeed
import com.example.ui.ChurchViewModel
import com.example.ui.components.CupertinoIcons
import com.example.ui.components.IosGroupedCard
import com.example.ui.components.IosSegmentedControl
import com.example.ui.components.IosTopBar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerGroupsScreen(
    viewModel: ChurchViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val joinedGroups by viewModel.joinedGroups.collectAsState()
    val prayerRequests by viewModel.prayerRequests.collectAsState()

    val tabs = listOf("Area Prayer Groups", "Community Prayer Wall")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val areas = listOf("All Areas", "North District", "Downtown / Central", "Westside", "East Valley", "South Hills")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        IosTopBar(
            title = "Prayer & Groups",
            subtitle = "COMMUNITY FELLOWSHIP",
            titleIcon = CupertinoIcons.Person2Fill,
            actions = {
                if (selectedTabIndex == 1) {
                    IconButton(
                        onClick = { viewModel.openPrayerModal() },
                        modifier = Modifier.testTag("add_prayer_request_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Submit Prayer Request",
                            tint = RoyalNavy
                        )
                    }
                }
            }
        )

        IosSegmentedControl(
            items = tabs,
            selectedIndex = selectedTabIndex,
            onSelectedIndexChanged = { selectedTabIndex = it },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
        )

        when (selectedTabIndex) {
            0 -> {
                // Prayer Groups by Area
                val filteredGroups = if (uiState.selectedAreaFilter == "All Areas") {
                    ChurchDataSeed.prayerGroups
                } else {
                    ChurchDataSeed.prayerGroups.filter { it.area == uiState.selectedAreaFilter }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Area Filter Chips
                    item {
                        Column {
                            Text(
                                text = "Filter by Location / District",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(areas) { area ->
                                    val isSelected = area == uiState.selectedAreaFilter
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.setAreaFilter(area) },
                                        label = { Text(area) },
                                        leadingIcon = if (isSelected) {
                                            {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        } else null,
                                        modifier = Modifier.testTag("area_chip_${area.lowercase().replace(" ", "_")}")
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Prayer Fellowships (${filteredGroups.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    items(filteredGroups) { group ->
                        val isJoined = joinedGroups.contains(group.id)
                        PrayerGroupItemCard(
                            group = group,
                            isJoined = isJoined,
                            onToggleJoin = { viewModel.toggleJoinGroup(group.id) },
                            onSendReminder = {
                                viewModel.triggerTestMeetingPush(context, group)
                            },
                            onEmailLeader = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:${group.leaderContact}")
                                    putExtra(Intent.EXTRA_SUBJECT, "Grace Church Prayer Group: ${group.name}")
                                }
                                context.startActivity(intent)
                            },
                            onOpenMap = {
                                val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(group.address)}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                context.startActivity(mapIntent)
                            }
                        )
                    }
                }
            }
            1 -> {
                // Community Prayer Wall
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Call to Action Banner
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = PrayerAccent.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrayerAccent.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Bearing One Another's Burdens",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = PrayerAccent
                                    )
                                    Text(
                                        text = "Tap 'I Prayed' to stand in faith with your church brothers & sisters.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Button(
                                    onClick = { viewModel.openPrayerModal() },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("submit_prayer_banner_btn")
                                ) {
                                    Text("Post Request", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Live Prayer Requests (${prayerRequests.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    items(prayerRequests) { req ->
                        PrayerRequestCard(
                            request = req,
                            onPray = { viewModel.prayForRequest(req.id) }
                        )
                    }
                }
            }
        }
    }

    // Modal to Submit Prayer Request
    if (uiState.isShowingPrayerModal) {
        CreatePrayerRequestDialog(
            onDismiss = { viewModel.closePrayerModal() },
            onSubmit = { author, isAnon, area, title, details ->
                viewModel.submitPrayerRequest(author, isAnon, area, title, details)
            }
        )
    }
}

@Composable
fun PrayerGroupItemCard(
    group: PrayerGroup,
    isJoined: Boolean,
    onToggleJoin: () -> Unit,
    onSendReminder: () -> Unit,
    onEmailLeader: () -> Unit,
    onOpenMap: () -> Unit
) {
    IosGroupedCard(
        modifier = Modifier.testTag("prayer_group_card_${group.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrayerAccent.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "${group.area} • ${group.groupType}",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrayerAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                if (isJoined) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = RoyalNavy
                    ) {
                        Text(
                            text = "RSVP Confirmed",
                            style = MaterialTheme.typography.labelSmall,
                            color = ChurchGoldLight,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = group.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Time and Location
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = ChurchGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = group.meetingDayTime,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${group.locationName} (${group.address})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = group.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Leader: ${group.leaderName} • ${group.memberCount} members",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onToggleJoin,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isJoined) MaterialTheme.colorScheme.surfaceVariant else RoyalNavy,
                        contentColor = if (isJoined) MaterialTheme.colorScheme.onSurface else Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isJoined) "Leave Group" else "Join & RSVP",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onSendReminder,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Meeting Reminder Push",
                        tint = RoyalNavy,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onEmailLeader,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Leader",
                        tint = RoyalNavy,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onOpenMap,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Map Location",
                        tint = RoyalNavy,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PrayerRequestCard(
    request: PrayerRequestEntity,
    onPray: () -> Unit
) {
    IosGroupedCard(
        modifier = Modifier.testTag("prayer_request_card_${request.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (request.isAnonymous) "Anonymous Member" else request.authorName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (request.isAnswered) PrayerAccent.copy(alpha = 0.15f) else ScriptureAccent.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (request.isAnswered) "Answered Prayer!" else request.area,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (request.isAnswered) PrayerAccent else ScriptureAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = request.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = RoyalNavy
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = request.details,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Prayer Intercession Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = DevotionAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${request.prayerCount} praying",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = DevotionAccent
                    )
                }

                Button(
                    onClick = onPray,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DevotionAccent),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("pray_for_request_${request.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "I Prayed",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CreatePrayerRequestDialog(
    onDismiss: () -> Unit,
    onSubmit: (author: String, isAnon: Boolean, area: String, title: String, details: String) -> Unit
) {
    var author by remember { mutableStateOf("") }
    var isAnon by remember { mutableStateOf(false) }
    var area by remember { mutableStateOf("Downtown / Central") }
    var title by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }

    val areas = listOf("Downtown / Central", "North District", "Westside", "East Valley", "South Hills")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Submit Prayer Request",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isAnon,
                        onCheckedChange = { isAnon = it }
                    )
                    Text(
                        text = "Post as Anonymous",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (!isAnon) {
                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text("Your Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text(
                    text = "Area Fellowship:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(areas) { a ->
                        FilterChip(
                            selected = area == a,
                            onClick = { area = a },
                            label = { Text(a, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title (e.g. Healing for family)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Describe your prayer need...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && details.isNotBlank()) {
                        onSubmit(author, isAnon, area, title, details)
                    }
                },
                enabled = title.isNotBlank() && details.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
            ) {
                Text("Share with Church")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
