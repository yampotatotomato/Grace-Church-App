package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PastorMessageEntity
import com.example.data.model.Pastor
import com.example.data.model.Sermon
import com.example.data.repository.ChurchDataSeed
import com.example.ui.ChurchViewModel
import com.example.ui.components.CupertinoIcons
import com.example.ui.components.IosGroupedCard
import com.example.ui.components.IosSegmentedControl
import com.example.ui.components.IosTopBar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastorsScreen(
    viewModel: ChurchViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val pastorMessages by viewModel.pastorMessages.collectAsState()

    val tabs = listOf(
        "Directory",
        "Guidance (${pastorMessages.size})",
        "Sermons & Media"
    )
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    var selectedSermonForDetail by remember { mutableStateOf<Sermon?>(null) }
    var selectedGuidanceFilter by remember { mutableStateOf("All") } // "All", "Replied", "Received"

    val filteredPastors = remember(uiState.pastorSearchQuery, uiState.selectedPastorSpecialtyFilter) {
        ChurchDataSeed.pastors.filter { pastor ->
            val matchesQuery = uiState.pastorSearchQuery.isBlank() ||
                pastor.name.contains(uiState.pastorSearchQuery, ignoreCase = true) ||
                pastor.title.contains(uiState.pastorSearchQuery, ignoreCase = true) ||
                pastor.bio.contains(uiState.pastorSearchQuery, ignoreCase = true) ||
                pastor.specialty.any { it.contains(uiState.pastorSearchQuery, ignoreCase = true) }

            val matchesFilter = when (uiState.selectedPastorSpecialtyFilter) {
                "All Pastors" -> true
                "Senior Leadership" -> pastor.title.contains("Senior", ignoreCase = true) || pastor.title.contains("Executive", ignoreCase = true)
                "Biblical Counseling" -> pastor.specialty.any { it.contains("Counseling", ignoreCase = true) || it.contains("Care", ignoreCase = true) }
                "Youth & Young Adults" -> pastor.title.contains("Youth", ignoreCase = true) || pastor.specialty.any { it.contains("Young Adults", ignoreCase = true) }
                "Prayer & Worship" -> pastor.title.contains("Prayer", ignoreCase = true) || pastor.specialty.any { it.contains("Prayer", ignoreCase = true) }
                "Missions & Outreach" -> pastor.title.contains("Missions", ignoreCase = true) || pastor.specialty.any { it.contains("Missions", ignoreCase = true) }
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }

    val specialtyFilters = listOf(
        "All Pastors",
        "Senior Leadership",
        "Biblical Counseling",
        "Youth & Young Adults",
        "Prayer & Worship",
        "Missions & Outreach"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        IosTopBar(
            title = "Pastoral Directory",
            subtitle = "MINISTRY CARE & GUIDANCE",
            actions = {
                IconButton(
                    onClick = { viewModel.openGuidanceComposer() },
                    modifier = Modifier.testTag("new_guidance_request_button")
                ) {
                    Icon(
                        imageVector = CupertinoIcons.SquareAndPencil,
                        contentDescription = "New Guidance Request",
                        tint = MaterialTheme.colorScheme.primary
                    )
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
                // 1. PASTORAL DIRECTORY TAB
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Search Bar
                    item {
                        OutlinedTextField(
                            value = uiState.pastorSearchQuery,
                            onValueChange = { viewModel.setPastorSearchQuery(it) },
                            placeholder = { Text("Search pastors by name, specialty, or role...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                if (uiState.pastorSearchQuery.isNotBlank()) {
                                    IconButton(onClick = { viewModel.setPastorSearchQuery("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear search",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pastor_search_bar")
                        )
                    }

                    // Specialty Filter Chips
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(specialtyFilters) { filter ->
                                val isSelected = uiState.selectedPastorSpecialtyFilter == filter
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setPastorSpecialtyFilter(filter) },
                                    label = {
                                        Text(
                                            text = filter,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 12.sp
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = RoyalNavy,
                                        selectedLabelColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.testTag("pastor_filter_${filter.lowercase().replace(" ", "_")}")
                                )
                            }
                        }
                    }

                    // Section Banner
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = ChurchGold.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(ChurchGold),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = RoyalNavy,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Grace Church Pastoral Team",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalNavy
                                    )
                                    Text(
                                        text = "Our pastors are here to shepherd, pray, and guide you in biblical counseling and spiritual growth.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    if (filteredPastors.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.PersonSearch,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "No pastors matched your filter",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    TextButton(onClick = {
                                        viewModel.setPastorSearchQuery("")
                                        viewModel.setPastorSpecialtyFilter("All Pastors")
                                    }) {
                                        Text("Reset Filters")
                                    }
                                }
                            }
                        }
                    } else {
                        items(filteredPastors) { pastor ->
                            PastorDetailedCard(
                                pastor = pastor,
                                onReachOutForGuidance = { viewModel.openGuidanceComposer(pastor) },
                                onViewProfile = { viewModel.openPastorProfile(pastor) },
                                onCall = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${pastor.phone}"))
                                    context.startActivity(intent)
                                },
                                onEmail = {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:${pastor.email}")
                                        putExtra(Intent.EXTRA_SUBJECT, "Grace Church Pastoral Guidance Inquiry")
                                    }
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }

            1 -> {
                // 2. DIRECT GUIDANCE & MESSAGES TAB
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Banner
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp)),
                            colors = CardDefaults.cardColors(containerColor = RoyalNavy)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "DIRECT PASTORAL GUIDANCE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ChurchGoldLight,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Confidential Guidance & Prayer",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Reach out for biblical counsel, life decisions, or spiritual encouragement.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                                Button(
                                    onClick = { viewModel.openGuidanceComposer() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ChurchGold,
                                        contentColor = RoyalNavy
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                    modifier = Modifier.testTag("compose_guidance_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("New Request", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Message List Status Filter
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("All", "Guidance Provided", "Awaiting Reply").forEach { filter ->
                                val isSelected = selectedGuidanceFilter == filter
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedGuidanceFilter = filter },
                                    label = { Text(filter, fontSize = 12.sp) },
                                    shape = RoundedCornerShape(16.dp)
                                )
                            }
                        }
                    }

                    val filteredMessages = pastorMessages.filter { msg ->
                        when (selectedGuidanceFilter) {
                            "All" -> true
                            "Guidance Provided" -> msg.pastorReply.isNotBlank() || msg.responseStatus == "Guidance Provided"
                            "Awaiting Reply" -> msg.pastorReply.isBlank() && msg.responseStatus != "Guidance Provided"
                            else -> true
                        }
                    }

                    if (filteredMessages.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.ChatBubbleOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "No guidance conversations yet",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Select any pastor from the directory to ask a question or request prayer.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(
                                        onClick = { selectedTabIndex = 0 },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
                                    ) {
                                        Text("Browse Pastoral Directory")
                                    }
                                }
                            }
                        }
                    } else {
                        items(filteredMessages, key = { it.id }) { message ->
                            PastorGuidanceThreadCard(
                                message = message,
                                onSendFollowUp = { followUp ->
                                    viewModel.replyToGuidanceThread(context, message, followUp)
                                },
                                onDelete = {
                                    viewModel.deleteGuidanceMessage(message.id)
                                }
                            )
                        }
                    }
                }
            }

            2 -> {
                // 3. SERMONS & MEDIA TAB
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Currently Playing Audio Player Bar
                    if (uiState.activeSermon != null) {
                        item {
                            ActiveAudioPlayerCard(
                                sermon = uiState.activeSermon!!,
                                isPlaying = uiState.isAudioPlaying,
                                progress = uiState.audioProgress,
                                speed = uiState.audioSpeed,
                                onTogglePlayPause = { viewModel.togglePlayPause() },
                                onSeek = { viewModel.seekAudio(it) },
                                onSpeedCycle = { viewModel.cycleAudioSpeed() },
                                onOpenDetails = { selectedSermonForDetail = uiState.activeSermon }
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Recent Pastoral Messages & Expository Sermons",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    items(ChurchDataSeed.sermons) { sermon ->
                        SermonListItem(
                            sermon = sermon,
                            isPlaying = uiState.isAudioPlaying && uiState.activeSermon?.id == sermon.id,
                            onPlay = { viewModel.playSermon(sermon) },
                            onClick = { selectedSermonForDetail = sermon }
                        )
                    }
                }
            }
        }
    }

    // Modal: Pastor Detailed Profile Sheet
    if (uiState.isShowingPastorProfileModal && uiState.selectedPastorForProfile != null) {
        val pastor = uiState.selectedPastorForProfile!!
        PastorProfileBottomSheet(
            pastor = pastor,
            onDismiss = { viewModel.closePastorProfile() },
            onReachOut = {
                viewModel.closePastorProfile()
                viewModel.openGuidanceComposer(pastor)
            },
            onCall = {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${pastor.phone}"))
                context.startActivity(intent)
            },
            onEmail = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:${pastor.email}")
                    putExtra(Intent.EXTRA_SUBJECT, "Grace Church Pastoral Inquiry")
                }
                context.startActivity(intent)
            },
            onPlaySermon = { sermon ->
                viewModel.playSermon(sermon)
                viewModel.closePastorProfile()
            }
        )
    }

    // Modal: Direct Guidance Request Composer
    if (uiState.isShowingGuidanceComposerModal && uiState.selectedPastorForContact != null) {
        val pastor = uiState.selectedPastorForContact!!
        GuidanceComposerDialog(
            pastor = pastor,
            prefillPrompt = uiState.prefillGuidancePrompt,
            prefillCategory = uiState.prefillGuidanceCategory,
            onDismiss = { viewModel.closeGuidanceComposer() },
            onSend = { senderName, senderEmail, category, urgency, subject, content ->
                viewModel.sendGuidanceMessage(
                    context = context,
                    pastor = pastor,
                    senderName = senderName,
                    senderEmail = senderEmail,
                    category = category,
                    urgency = urgency,
                    subject = subject,
                    content = content
                )
            }
        )
    }

    // Modal: Sermon Detail Notes Modal
    if (selectedSermonForDetail != null) {
        val s = selectedSermonForDetail!!
        ModalBottomSheet(
            onDismissRequest = { selectedSermonForDetail = null },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = s.seriesName.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = PastorAccent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = s.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${s.pastorName} (${s.pastorTitle}) • ${s.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "SCRIPTURE FOCUS: ${s.scriptureReference}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = ScriptureAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = s.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Key Theological Takeaways:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                s.keyPoints.forEach { point ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("• ", fontWeight = FontWeight.Bold, color = PastorAccent)
                        Text(
                            text = point,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        viewModel.playSermon(s)
                        selectedSermonForDetail = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Play Sermon Audio (${s.durationMinutes} min)")
                }
            }
        }
    }
}

@Composable
fun PastorDetailedCard(
    pastor: Pastor,
    onReachOutForGuidance: () -> Unit,
    onViewProfile: () -> Unit,
    onCall: () -> Unit,
    onEmail: () -> Unit
) {
    IosGroupedCard(
        modifier = Modifier.testTag("pastor_card_${pastor.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Pastor Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pastor Monogram Avatar
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(RoyalNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = pastor.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ChurchGoldLight
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = pastor.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        // Availability Status Dot
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF34C759))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Available",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Text(
                        text = pastor.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = ChurchGold,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (pastor.yearsOfMinistry.isNotBlank()) {
                        Text(
                            text = pastor.yearsOfMinistry,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bio Summary
            Text(
                text = pastor.bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Specialties / Ministry Focus Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                pastor.specialty.forEach { spec ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = RoyalNavy.copy(alpha = 0.08f)
                    ) {
                        Text(
                            text = spec,
                            style = MaterialTheme.typography.labelSmall,
                            color = RoyalNavy,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Office Hours & Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = pastor.officeHours,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (pastor.officeLocation.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = pastor.officeLocation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onReachOutForGuidance,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("reach_out_button_${pastor.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reach Out for Guidance",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                OutlinedButton(
                    onClick = onViewProfile,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("view_profile_button_${pastor.id}")
                ) {
                    Text("Profile", style = MaterialTheme.typography.labelMedium)
                }

                IconButton(
                    onClick = onCall,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call ${pastor.name}",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onEmail,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mail,
                        contentDescription = "Email ${pastor.name}",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PastorGuidanceThreadCard(
    message: PastorMessageEntity,
    onSendFollowUp: (String) -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var isExpandedForReply by remember { mutableStateOf(false) }
    var followUpText by remember { mutableStateOf("") }

    IosGroupedCard(
        modifier = Modifier
            .testTag("guidance_thread_${message.id}")
            .animateContentSize()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Pastor info & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(RoyalNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = message.pastorName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = ChurchGoldLight
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "To: ${message.pastorName}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = RoyalNavy
                        )
                        Text(
                            text = message.pastorTitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                val statusColor = when (message.responseStatus) {
                    "Guidance Provided" -> Color(0xFF2E7D32)
                    "In Active Dialogue" -> RoyalNavy
                    else -> PrayerAccent
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (message.pastorReply.isNotBlank()) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = message.responseStatus,
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category & Subject
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ChurchGold.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = message.messageType,
                        style = MaterialTheme.typography.labelSmall,
                        color = ChurchGold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 11.sp
                    )
                }

                if (message.urgency != "Standard") {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Red.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = message.urgency,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFC62828),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = message.subject,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // User's inquiry text
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "You asked (${message.senderName}):",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Pastoral Reply & Scripture Guidance
            if (message.pastorReply.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = RoyalNavy.copy(alpha = 0.05f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ChurchGold.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = null,
                                tint = ChurchGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Pastoral Counsel from ${message.pastorName}:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = RoyalNavy
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = message.pastorReply,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        )

                        if (message.scriptureGuidance.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ChurchGold.copy(alpha = 0.12f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "ANCHOR SCRIPTURE:",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = ChurchGold,
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = message.scriptureGuidance,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontStyle = FontStyle.Italic,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Scripture Guidance", message.scriptureGuidance)
                                            clipboard.setPrimaryClip(clip)
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy Scripture",
                                            tint = ChurchGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row: Reply / Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { isExpandedForReply = !isExpandedForReply },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (isExpandedForReply) Icons.Default.ExpandLess else Icons.Default.Reply,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = RoyalNavy
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isExpandedForReply) "Close Reply" else "Reply to Pastor",
                        color = RoyalNavy,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Message",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Expandable Follow-up Reply Composer
            AnimatedVisibility(visible = isExpandedForReply) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    OutlinedTextField(
                        value = followUpText,
                        onValueChange = { followUpText = it },
                        placeholder = { Text("Write follow-up question or gratitude to ${message.pastorName}...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (followUpText.isNotBlank()) {
                                onSendFollowUp(followUpText)
                                followUpText = ""
                                isExpandedForReply = false
                            }
                        },
                        enabled = followUpText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Send Follow-Up")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastorProfileBottomSheet(
    pastor: Pastor,
    onDismiss: () -> Unit,
    onReachOut: () -> Unit,
    onCall: () -> Unit,
    onEmail: () -> Unit,
    onPlaySermon: (Sermon) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .padding(bottom = 32.dp)
        ) {
            // Profile Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(RoyalNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = pastor.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = ChurchGoldLight
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pastor.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = pastor.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ChurchGold,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (pastor.yearsOfMinistry.isNotBlank()) {
                        Text(
                            text = pastor.yearsOfMinistry,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (pastor.education.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = RoyalNavy,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = pastor.education,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bio & Calling
            Text(
                text = "ABOUT PASTORAL CALLING",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = ChurchGold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = pastor.bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Favorite Scripture
            if (pastor.favoriteScripture.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ChurchGold.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = ChurchGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "FAVORITE LIFE SCRIPTURE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ChurchGold,
                                fontSize = 10.sp
                            )
                            Text(
                                text = pastor.favoriteScripture,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = RoyalNavy
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Guidance Prompt Ideas
            if (pastor.guidancePromptStarters.isNotEmpty()) {
                Text(
                    text = "FREQUENT GUIDANCE TOPICS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                pastor.guidancePromptStarters.forEach { prompt ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = ChurchGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Quick Reach Out CTA
            Button(
                onClick = onReachOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("modal_reach_out_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reach Out to ${pastor.name} for Guidance", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onCall,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call Office")
                }

                OutlinedButton(
                    onClick = onEmail,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Mail, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Send Email")
                }
            }
        }
    }
}

@Composable
fun GuidanceComposerDialog(
    pastor: Pastor,
    prefillPrompt: String = "",
    prefillCategory: String = "Spiritual Guidance & Discernment",
    onDismiss: () -> Unit,
    onSend: (senderName: String, senderEmail: String, category: String, urgency: String, subject: String, content: String) -> Unit
) {
    var senderName by remember { mutableStateOf("") }
    var senderEmail by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(prefillCategory) }
    var urgency by remember { mutableStateOf("Standard") }
    var subject by remember { mutableStateOf(if (prefillPrompt.isNotBlank()) prefillPrompt else "") }
    var content by remember { mutableStateOf("") }

    val categories = listOf(
        "Spiritual Guidance & Discernment",
        "Biblical Counseling",
        "Confidential Prayer Request",
        "Grief & Life Transitions",
        "Marriage & Family Guidance",
        "Scripture / Theology Question"
    )

    val urgencies = listOf("Standard", "Urgent Care", "Confidential Meeting")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Reach Out for Guidance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Direct to ${pastor.name} (${pastor.title})",
                    style = MaterialTheme.typography.bodySmall,
                    color = ChurchGold,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 440.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Guidance Category",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Urgency / Type",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        urgencies.forEach { urg ->
                            FilterChip(
                                selected = urgency == urg,
                                onClick = { urgency = urg },
                                label = { Text(urg, fontSize = 11.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject / Main Focus") },
                        placeholder = { Text("e.g. Navigating difficult career choice...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = senderName,
                        onValueChange = { senderName = it },
                        label = { Text("Your Name") },
                        placeholder = { Text("e.g. John Doe") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = senderEmail,
                        onValueChange = { senderEmail = it },
                        label = { Text("Your Email or Phone") },
                        placeholder = { Text("contact@example.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Your Guidance Details or Prayer Needs") },
                        placeholder = { Text("Share your situation, questions, or what you'd like biblical counsel on...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5
                    )
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Pastoral communications are treated with strict confidentiality.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (content.isNotBlank()) {
                        onSend(
                            if (senderName.isBlank()) "Church Member" else senderName,
                            if (senderEmail.isBlank()) "member@gracechurch.org" else senderEmail,
                            category,
                            urgency,
                            if (subject.isBlank()) "Guidance on $category" else subject,
                            content
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                enabled = content.isNotBlank(),
                modifier = Modifier.testTag("submit_guidance_request_button")
            ) {
                Text("Send Directly to Pastor")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ActiveAudioPlayerCard(
    sermon: Sermon,
    isPlaying: Boolean,
    progress: Float,
    speed: Float,
    onTogglePlayPause: () -> Unit,
    onSeek: (Float) -> Unit,
    onSpeedCycle: () -> Unit,
    onOpenDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = RoyalNavy)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "NOW PLAYING SERMON",
                        style = MaterialTheme.typography.labelSmall,
                        color = ChurchGoldLight,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = sermon.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${sermon.pastorName} • ${sermon.scriptureReference}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                IconButton(
                    onClick = onOpenDetails,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Sermon Notes",
                        tint = ChurchGoldLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Slider(
                value = progress,
                onValueChange = onSeek,
                colors = SliderDefaults.colors(
                    thumbColor = ChurchGold,
                    activeTrackColor = ChurchGold,
                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onSpeedCycle,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${speed}x",
                        color = ChurchGoldLight,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onSeek((progress - 0.05f).coerceAtLeast(0f)) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "Rewind 10s",
                            tint = Color.White
                        )
                    }

                    FilledIconButton(
                        onClick = onTogglePlayPause,
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = ChurchGold),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = RoyalNavy,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(
                        onClick = { onSeek((progress + 0.05f).coerceAtMost(1f)) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "Forward 10s",
                            tint = Color.White
                        )
                    }
                }

                Text(
                    text = "${(sermon.durationMinutes * progress).toInt()} / ${sermon.durationMinutes}m",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun SermonListItem(
    sermon: Sermon,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onClick: () -> Unit
) {
    IosGroupedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("sermon_item_${sermon.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isPlaying) ChurchGold else RoyalNavy.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onPlay) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play ${sermon.title}",
                        tint = if (isPlaying) RoyalNavy else RoyalNavy
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sermon.seriesName.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = PastorAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Text(
                    text = sermon.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${sermon.pastorName} • ${sermon.scriptureReference} • ${sermon.durationMinutes} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View notes",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

