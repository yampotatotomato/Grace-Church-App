package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.Pastor
import com.example.data.model.Sermon
import com.example.data.repository.ChurchDataSeed
import com.example.ui.ChurchViewModel
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

    val tabs = listOf("Sermons & Media", "Pastoral Directory", "My Messages")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    var selectedSermonForDetail by remember { mutableStateOf<Sermon?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        IosTopBar(
            title = "Pastors & Media",
            subtitle = "MINISTRY TEACHINGS & CARE"
        )

        IosSegmentedControl(
            items = tabs,
            selectedIndex = selectedTabIndex,
            onSelectedIndexChanged = { selectedTabIndex = it },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        when (selectedTabIndex) {
            0 -> {
                // Sermons List
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
                            text = "Recent Pastoral Messages",
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
            1 -> {
                // Pastoral Directory
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Grace Church Pastoral Team",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Our pastors are here to shepherd, pray, and guide you in biblical counseling.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(ChurchDataSeed.pastors) { pastor ->
                        PastorDirectoryCard(
                            pastor = pastor,
                            onContact = { viewModel.openPastorContactModal(pastor) },
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
                            }
                        )
                    }
                }
            }
            2 -> {
                // Sent Messages History
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "My Messages to Pastors",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    if (pastorMessages.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.MailOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "No messages sent yet",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "You can reach out to any pastor in the directory for prayer or counseling.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(pastorMessages) { msg ->
                            IosGroupedCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "To: ${msg.pastorName}",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalNavy
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = PrayerAccent.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = msg.responseStatus,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = PrayerAccent,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Type: ${msg.messageType}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ChurchGold,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = msg.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Sermon Detail Modal
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

    // Pastor Contact & Counseling Modal
    if (uiState.isShowingPastorContactModal && uiState.selectedPastorForContact != null) {
        val pastor = uiState.selectedPastorForContact!!
        PastorContactDialog(
            pastor = pastor,
            onDismiss = { viewModel.closePastorContactModal() },
            onSend = { senderName, senderEmail, type, content ->
                viewModel.sendPastorMessage(context, pastor, senderName, senderEmail, type, content)
            }
        )
    }
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
            .clip(RoundedCornerShape(20.dp))
            .testTag("active_audio_player_card"),
        colors = CardDefaults.cardColors(containerColor = RoyalNavy),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "NOW PLAYING",
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
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                IconButton(onClick = onOpenDetails) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Sermon Study Notes",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Scrubber
            Slider(
                value = progress,
                onValueChange = onSeek,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = ChurchGoldLight,
                    activeTrackColor = ChurchGold,
                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val currentMin = ((sermon.durationMinutes * 60 * progress) / 60).toInt()
                val currentSec = ((sermon.durationMinutes * 60 * progress) % 60).toInt()
                Text(
                    text = String.format("%02d:%02d", currentMin, currentSec),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                    text = "${sermon.durationMinutes}:00",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Player Action Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speed Button
                TextButton(
                    onClick = onSpeedCycle,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                ) {
                    Text(
                        text = "${speed}x",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Skip 15s Back
                IconButton(
                    onClick = { onSeek((progress - 0.05f).coerceAtLeast(0f)) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay10,
                        contentDescription = "Rewind 10 seconds",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Play / Pause Circle Button
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(ChurchGold)
                        .clickable(onClick = onTogglePlayPause)
                        .testTag("audio_play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = RoyalNavy,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Skip 15s Forward
                IconButton(
                    onClick = { onSeek((progress + 0.05f).coerceAtMost(1f)) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Forward10,
                        contentDescription = "Forward 10 seconds",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Bookmark / Heart Button
                IconButton(onClick = onOpenDetails) {
                    Icon(
                        imageVector = Icons.Default.Article,
                        contentDescription = "View Outline",
                        tint = Color.White
                    )
                }
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
        onClick = onClick,
        modifier = Modifier.testTag("sermon_item_${sermon.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isPlaying) ChurchGold else PastorAccent.copy(alpha = 0.15f))
                    .clickable(onClick = onPlay),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = if (isPlaying) RoyalNavy else PastorAccent,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sermon.seriesName.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = PastorAccent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = sermon.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${sermon.pastorName} • ${sermon.durationMinutes} min • ${sermon.scriptureReference}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PastorDirectoryCard(
    pastor: Pastor,
    onContact: () -> Unit,
    onCall: () -> Unit,
    onEmail: () -> Unit
) {
    IosGroupedCard(
        modifier = Modifier.testTag("pastor_card_${pastor.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
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
                    Text(
                        text = pastor.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = pastor.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = ChurchGold,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = pastor.bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Office Hours: ${pastor.officeHours}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons (Contact Pastor, Direct Call, Direct Email)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onContact,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Message / Pray", style = MaterialTheme.typography.labelMedium)
                }

                OutlinedButton(
                    onClick = onCall,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
                }

                OutlinedButton(
                    onClick = onEmail,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Mail, contentDescription = "Email", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun PastorContactDialog(
    pastor: Pastor,
    onDismiss: () -> Unit,
    onSend: (senderName: String, senderEmail: String, messageType: String, content: String) -> Unit
) {
    var senderName by remember { mutableStateOf("") }
    var senderEmail by remember { mutableStateOf("") }
    var messageType by remember { mutableStateOf("Prayer Request") }
    var content by remember { mutableStateOf("") }

    val types = listOf("Prayer Request", "Biblical Counseling", "Pastoral Meeting", "General Question")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Contact ${pastor.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = pastor.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = ChurchGold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = senderName,
                    onValueChange = { senderName = it },
                    label = { Text("Your Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = senderEmail,
                    onValueChange = { senderEmail = it },
                    label = { Text("Your Email or Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Request Category",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    types.take(2).forEach { t ->
                        FilterChip(
                            selected = messageType == t,
                            onClick = { messageType = t },
                            label = { Text(t, fontSize = 11.sp) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    types.drop(2).forEach { t ->
                        FilterChip(
                            selected = messageType == t,
                            onClick = { messageType = t },
                            label = { Text(t, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Your message or prayer request details...") },
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
                    if (content.isNotBlank()) {
                        onSend(
                            if (senderName.isBlank()) "Church Member" else senderName,
                            if (senderEmail.isBlank()) "member@gracechurch.org" else senderEmail,
                            messageType,
                            content
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                enabled = content.isNotBlank()
            ) {
                Text("Send to Pastor")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
