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
import com.example.ui.components.AppleGlassCard
import com.example.ui.components.AppleGlassPill
import com.example.ui.components.AppleMovingAtmosphereBackground
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

    val tabs = listOf("Discover Prayer Groups", "Community Prayer Wall")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val categories = listOf(
        "All Categories",
        "Young Adults",
        "Families & Couples",
        "Men's Fellowship",
        "Women's Grace",
        "Intercessory Prayer",
        "Bible Study",
        "Home Fellowship",
        "Seniors Ministry"
    )

    val areas = listOf(
        "All Areas",
        "Downtown / Central",
        "North District",
        "Westside",
        "East Valley",
        "South Hills",
        "Online / Global"
    )

    val formats = listOf("All Formats", "In-Person", "Home Gathering", "Hybrid / Virtual")
    val sortOptions = listOf("Recommended", "Nearest Distance", "Most Members")

    // Atmospheric Apple Moving Background for the entire Community section
    AppleMovingAtmosphereBackground(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            IosTopBar(
                title = "Prayer & Groups",
                subtitle = "COMMUNITY FELLOWSHIP • APPLE GLASS",
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
                    // Location-based and Categorized Prayer Groups Discovery
                    val allGroups = ChurchDataSeed.prayerGroups
                    val filteredGroups = remember(
                        uiState.prayerGroupSearchQuery,
                        uiState.selectedAreaFilter,
                        uiState.selectedGroupCategoryFilter,
                        uiState.selectedGroupFormatFilter,
                        uiState.selectedGroupDayFilter,
                        uiState.prayerGroupSortBy,
                        uiState.showJoinedGroupsOnly,
                        joinedGroups
                    ) {
                        allGroups.filter { group ->
                            val q = uiState.prayerGroupSearchQuery.trim().lowercase()
                            val matchesQuery = if (q.isBlank()) {
                                true
                            } else {
                                group.name.lowercase().contains(q) ||
                                group.description.lowercase().contains(q) ||
                                group.locationName.lowercase().contains(q) ||
                                group.address.lowercase().contains(q) ||
                                group.leaderName.lowercase().contains(q) ||
                                group.category.lowercase().contains(q) ||
                                group.area.lowercase().contains(q) ||
                                group.groupType.lowercase().contains(q) ||
                                group.tags.any { it.lowercase().contains(q) }
                            }

                            val matchesArea = uiState.selectedAreaFilter == "All Areas" ||
                                group.area.equals(uiState.selectedAreaFilter, ignoreCase = true)

                            val matchesCategory = uiState.selectedGroupCategoryFilter == "All Categories" ||
                                group.category.equals(uiState.selectedGroupCategoryFilter, ignoreCase = true) ||
                                group.groupType.equals(uiState.selectedGroupCategoryFilter, ignoreCase = true)

                            val matchesFormat = uiState.selectedGroupFormatFilter == "All Formats" ||
                                group.meetingFormat.contains(uiState.selectedGroupFormatFilter, ignoreCase = true)

                            val matchesDay = uiState.selectedGroupDayFilter == "Any Day" ||
                                group.dayOfWeek.equals(uiState.selectedGroupDayFilter, ignoreCase = true) ||
                                group.meetingDayTime.contains(uiState.selectedGroupDayFilter, ignoreCase = true)

                            val matchesJoined = if (uiState.showJoinedGroupsOnly) {
                                joinedGroups.contains(group.id)
                            } else true

                            matchesQuery && matchesArea && matchesCategory && matchesFormat && matchesDay && matchesJoined
                        }.let { list ->
                            when (uiState.prayerGroupSortBy) {
                                "Nearest Distance" -> list.sortedBy { it.distanceMiles }
                                "Most Members" -> list.sortedByDescending { it.memberCount }
                                else -> list // "Recommended"
                            }
                        }
                    }

                    val hasActiveFilters = uiState.prayerGroupSearchQuery.isNotBlank() ||
                        uiState.selectedAreaFilter != "All Areas" ||
                        uiState.selectedGroupCategoryFilter != "All Categories" ||
                        uiState.selectedGroupFormatFilter != "All Formats" ||
                        uiState.showJoinedGroupsOnly

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        contentPadding = PaddingValues(top = 6.dp, bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 1. Search Bar with Apple Glass Translucency
                        item {
                            AppleGlassCard(
                                elevation = 4.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = uiState.prayerGroupSearchQuery,
                                    onValueChange = { viewModel.setPrayerGroupSearchQuery(it) },
                                    placeholder = {
                                        Text(
                                            "Search by name, neighborhood, leader, topic...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search",
                                            tint = RoyalNavy
                                        )
                                    },
                                    trailingIcon = {
                                        if (uiState.prayerGroupSearchQuery.isNotEmpty()) {
                                            IconButton(onClick = { viewModel.setPrayerGroupSearchQuery("") }) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "Clear Search",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = RoyalNavy,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("prayer_group_search_input")
                                )
                            }
                        }

                        // 2. Categorized Search Filter Chips
                        item {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Ministry Category",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (uiState.selectedGroupCategoryFilter != "All Categories") {
                                        Text(
                                            text = "Reset",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = RoyalNavy,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.clickable { viewModel.setGroupCategoryFilter("All Categories") }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(categories) { cat ->
                                        val isSelected = cat == uiState.selectedGroupCategoryFilter
                                        AppleGlassPill(
                                            text = cat,
                                            isSelected = isSelected,
                                            selectedColor = RoyalNavy,
                                            icon = if (isSelected) {
                                                {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                }
                                            } else null,
                                            onClick = { viewModel.setGroupCategoryFilter(cat) },
                                            testTag = "category_chip_${cat.lowercase().replace(" ", "_").replace("&", "and")}"
                                        )
                                    }
                                }
                            }
                        }

                        // 3. Location & Area Filter Chips
                        item {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Location & District",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (uiState.selectedAreaFilter != "All Areas") {
                                        Text(
                                            text = "Reset",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = RoyalNavy,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.clickable { viewModel.setAreaFilter("All Areas") }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(areas) { area ->
                                        val isSelected = area == uiState.selectedAreaFilter
                                        AppleGlassPill(
                                            text = if (area == "All Areas") "All Locations" else "📍 $area",
                                            isSelected = isSelected,
                                            selectedColor = PrayerAccent,
                                            icon = if (isSelected) {
                                                {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                }
                                            } else null,
                                            onClick = { viewModel.setAreaFilter(area) },
                                            testTag = "area_chip_${area.lowercase().replace(" ", "_").replace("/", "")}"
                                        )
                                    }
                                }
                            }
                        }

                        // 4. Format & Quick Toggles Row
                        item {
                            Column {
                                Text(
                                    text = "Meeting Format & Preferences",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Joined Only Filter
                                    item {
                                        AppleGlassPill(
                                            text = "My Groups (${joinedGroups.size})",
                                            isSelected = uiState.showJoinedGroupsOnly,
                                            selectedColor = ChurchGoldDark,
                                            icon = if (uiState.showJoinedGroupsOnly) {
                                                {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                }
                                            } else null,
                                            onClick = { viewModel.toggleShowJoinedGroupsOnly() },
                                            testTag = "filter_joined_only"
                                        )
                                    }

                                    items(formats) { format ->
                                        val isSelected = format == uiState.selectedGroupFormatFilter
                                        AppleGlassPill(
                                            text = format,
                                            isSelected = isSelected,
                                            selectedColor = RoyalNavy,
                                            onClick = { viewModel.setGroupFormatFilter(format) },
                                            testTag = "format_chip_${format.lowercase().replace(" ", "_").replace("/", "")}"
                                        )
                                    }

                                    items(sortOptions) { sort ->
                                        val isSelected = sort == uiState.prayerGroupSortBy
                                        AppleGlassPill(
                                            text = "Sort: $sort",
                                            isSelected = isSelected,
                                            selectedColor = RoyalNavy,
                                            onClick = { viewModel.setGroupSortBy(sort) },
                                            testTag = "sort_chip_${sort.lowercase().replace(" ", "_")}"
                                        )
                                    }
                                }
                            }
                        }

                        // 5. Active Filters Summary & Results Counter
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Prayer Fellowships",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    AppleGlassPill(
                                        text = "${filteredGroups.size} found",
                                        isSelected = true,
                                        selectedColor = RoyalNavy
                                    )
                                }

                                if (hasActiveFilters) {
                                    TextButton(
                                        onClick = { viewModel.resetPrayerGroupFilters() },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.testTag("reset_prayer_filters_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = DevotionAccent
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Reset All",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = DevotionAccent,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // Empty State if no groups match filters
                        if (filteredGroups.isEmpty()) {
                            item {
                                AppleGlassCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SearchOff,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Text(
                                            text = "No Prayer Groups Found",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "No groups match your search or filter combination. Try adjusting keywords, switching to 'All Locations', or resetting filters.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                        Button(
                                            onClick = { viewModel.resetPrayerGroupFilters() },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                                            modifier = Modifier.testTag("empty_state_reset_filters")
                                        ) {
                                            Text("Show All Prayer Groups")
                                        }
                                    }
                                }
                            }
                        } else {
                            items(filteredGroups) { group ->
                                val isJoined = joinedGroups.contains(group.id)
                                PrayerGroupItemCard(
                                    group = group,
                                    isJoined = isJoined,
                                    onToggleJoin = { viewModel.toggleJoinGroup(group.id) },
                                    onOpenDetails = { viewModel.openPrayerGroupDetail(group) },
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
                                    },
                                    onShareGroup = {
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "Join me at ${group.name}!\nMeeting: ${group.meetingDayTime}\nLocation: ${group.locationName} (${group.address})\nLeader: ${group.leaderName} (${group.leaderContact})\nGrace Church Community"
                                            )
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Share Prayer Group"))
                                    }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Community Prayer Wall with Apple Glass Cards
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Call to Action Banner in Apple Glass
                        item {
                            AppleGlassCard(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
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

    // Detailed Prayer Group Discovery Modal in Apple Glass
    if (uiState.isShowingGroupDetailModal && uiState.selectedPrayerGroupForDetail != null) {
        val group = uiState.selectedPrayerGroupForDetail!!
        val isJoined = joinedGroups.contains(group.id)
        PrayerGroupDetailDialog(
            group = group,
            isJoined = isJoined,
            onDismiss = { viewModel.closePrayerGroupDetail() },
            onToggleJoin = { viewModel.toggleJoinGroup(group.id) },
            onSendReminder = { viewModel.triggerTestMeetingPush(context, group) },
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
            },
            onShareGroup = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Join me at ${group.name}!\nMeeting: ${group.meetingDayTime}\nLocation: ${group.locationName} (${group.address})\nLeader: ${group.leaderName} (${group.leaderContact})\nGrace Church Community"
                    )
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share Prayer Group"))
            }
        )
    }
}

@Composable
fun PrayerGroupItemCard(
    group: PrayerGroup,
    isJoined: Boolean,
    onToggleJoin: () -> Unit,
    onOpenDetails: () -> Unit = {},
    onSendReminder: () -> Unit,
    onEmailLeader: () -> Unit,
    onOpenMap: () -> Unit,
    onShareGroup: () -> Unit = {}
) {
    AppleGlassCard(
        elevation = 6.dp,
        onClick = onOpenDetails,
        testTag = "prayer_group_card_${group.id}",
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Badges Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PrayerAccent.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = group.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = PrayerAccent,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = if (group.distanceMiles > 0) "📍 ${group.distanceMiles} mi • ${group.area}" else "🌐 ${group.area}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                if (isJoined) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = RoyalNavy
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = ChurchGoldLight,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "RSVP Confirmed",
                                style = MaterialTheme.typography.labelSmall,
                                color = ChurchGoldLight,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = group.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Time and Schedule
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
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                ) {
                    Text(
                        text = group.meetingFormat,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Location with address
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = group.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Tags row
            if (group.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(group.tags) { tag ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = "#$tag",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val effectiveMembers = if (isJoined) group.memberCount + 1 else group.memberCount
            Text(
                text = "Leader: ${group.leaderName} • $effectiveMembers active members",
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
                    modifier = Modifier
                        .weight(1f)
                        .testTag("join_group_button_${group.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isJoined) MaterialTheme.colorScheme.surfaceVariant else RoyalNavy,
                        contentColor = if (isJoined) MaterialTheme.colorScheme.onSurface else Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (isJoined) Icons.Default.Check else Icons.Default.AddCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isJoined) "Leave Group" else "Join & RSVP",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onOpenDetails,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                        .testTag("group_details_button_${group.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "View Details",
                        tint = RoyalNavy,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onSendReminder,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                        .testTag("group_reminder_button_${group.id}")
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
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                        .testTag("group_email_button_${group.id}")
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
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                        .testTag("group_map_button_${group.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Map Location",
                        tint = RoyalNavy,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onShareGroup,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                        .testTag("group_share_button_${group.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Prayer Group",
                        tint = RoyalNavy,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PrayerGroupDetailDialog(
    group: PrayerGroup,
    isJoined: Boolean,
    onDismiss: () -> Unit,
    onToggleJoin: () -> Unit,
    onSendReminder: () -> Unit,
    onEmailLeader: () -> Unit,
    onOpenMap: () -> Unit,
    onShareGroup: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrayerAccent.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${group.category} • ${group.area}",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrayerAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                if (group.distanceMiles > 0) {
                    Text(
                        text = "📍 ${group.distanceMiles} mi away",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Schedule card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = ChurchGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = group.meetingDayTime,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = RoyalNavy,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = group.locationName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = group.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Text(
                    text = group.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )

                // Tags
                if (group.tags.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(group.tags) { tag ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "#$tag",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Leader details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Group Leader",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = group.leaderName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onEmailLeader,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Email Leader", fontSize = 11.sp)
                    }
                }

                // Map & Directions Quick Link
                OutlinedButton(
                    onClick = onOpenMap,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Get Directions in Maps")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onToggleJoin,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isJoined) MaterialTheme.colorScheme.surfaceVariant else RoyalNavy,
                    contentColor = if (isJoined) MaterialTheme.colorScheme.onSurface else Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = if (isJoined) Icons.Default.Check else Icons.Default.AddCircle,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isJoined) "Leave Group" else "Join & RSVP")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TextButton(onClick = onShareGroup) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share")
                }
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    )
}

@Composable
fun PrayerRequestCard(
    request: PrayerRequestEntity,
    onPray: () -> Unit
) {
    AppleGlassCard(
        elevation = 4.dp,
        testTag = "prayer_request_card_${request.id}",
        modifier = Modifier.fillMaxWidth()
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

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = request.details,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${request.prayerCount} praying",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = onPray,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DevotionAccent),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("pray_for_request_button_${request.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("I Prayed", style = MaterialTheme.typography.labelSmall)
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
