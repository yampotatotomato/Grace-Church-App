package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.JournalEntryEntity
import com.example.data.repository.ChurchDataSeed
import com.example.ui.ChurchTab
import com.example.ui.ChurchViewModel
import com.example.ui.components.CupertinoIcons
import com.example.ui.components.IosGroupedCard
import com.example.ui.components.IosTopBar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    viewModel: ChurchViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val journals by viewModel.journals.collectAsState()
    val favoriteDevotionIds by viewModel.favoriteDevotionIds.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Devotion Reflection", "Gratitude", "Prayer", "Scripture Study")

    // Filter journal entries
    val filteredJournals = remember(journals, searchQuery, selectedCategory) {
        journals.filter { entry ->
            val matchesCategory = selectedCategory == "All" || entry.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    entry.title.contains(searchQuery, ignoreCase = true) ||
                    entry.reflectionText.contains(searchQuery, ignoreCase = true) ||
                    entry.prayerText.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // iOS Top Bar with Cupertino Icons
        IosTopBar(
            title = "Spiritual Journal",
            subtitle = "SANCTUARY REFLECTIONS",
            actions = {
                IconButton(
                    onClick = { viewModel.openNewJournalModal() },
                    modifier = Modifier.testTag("journal_new_entry_top_button")
                ) {
                    Icon(
                        imageVector = CupertinoIcons.SquareAndPencil,
                        contentDescription = "New Entry",
                        tint = RoyalNavy
                    )
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
            // 1. Search Bar & Summary Card
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search prayers, verses, or thoughts...") },
                    leadingIcon = {
                        Icon(
                            imageVector = CupertinoIcons.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = CupertinoIcons.Xmark,
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = RoyalNavy,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("journal_search_input")
                )
            }

            // 2. Metrics & Quick Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = RoyalNavy.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalNavy.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(RoyalNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = CupertinoIcons.DocText,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${journals.size}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalNavy
                                )
                                Text(
                                    text = "Reflections",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = ChurchGold.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ChurchGold.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .weight(1f)
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
                                    imageVector = CupertinoIcons.HeartFill,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${favoriteDevotionIds.size}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = ChurchGold
                                )
                                Text(
                                    text = "Favorites",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 3. Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        Surface(
                            onClick = { selectedCategory = category },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) RoyalNavy else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) RoyalNavy else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.testTag("journal_category_$category")
                        ) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // 4. Quick Action to Compose New Entry
            item {
                Surface(
                    onClick = { viewModel.openNewJournalModal() },
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalNavy.copy(alpha = 0.25f)),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("journal_compose_bar")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(RoyalNavy.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = CupertinoIcons.Pencil,
                                    contentDescription = null,
                                    tint = RoyalNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Write a Spiritual Journal Entry",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Record answered prayers, scriptures, and insights",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            imageVector = CupertinoIcons.ChevronRight,
                            contentDescription = null,
                            tint = RoyalNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 5. Journal Entries List
            if (filteredJournals.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = CupertinoIcons.DocText,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No journal entries match \"$searchQuery\"" else "Your Spiritual Journal is Empty",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap above to write your thoughts, prayers, or devotional reflections.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 24.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(filteredJournals, key = { it.id }) { entry ->
                    JournalEntryCard(
                        entry = entry,
                        onEdit = { viewModel.openNewJournalModal(entry) },
                        onDelete = { viewModel.deleteJournalEntry(entry.id) }
                    )
                }
            }
        }
    }

    // Modal Sheet / Dialog to Compose or Edit Journal Entry
    if (uiState.isShowingNewJournalModal) {
        JournalEditorDialog(
            entry = uiState.editingJournalEntry,
            onDismiss = { viewModel.closeNewJournalModal() },
            onSave = { title, reflection, prayer, category, mood, id, devotionId ->
                viewModel.saveCustomJournal(
                    title = title,
                    reflectionText = reflection,
                    prayerText = prayer,
                    category = category,
                    mood = mood,
                    id = id,
                    devotionId = devotionId
                )
            }
        )
    }
}

@Composable
fun JournalEntryCard(
    entry: JournalEntryEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    IosGroupedCard(
        modifier = modifier.testTag("journal_card_${entry.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Tag
                val categoryColor = when (entry.category) {
                    "Gratitude" -> ChurchGold
                    "Prayer" -> PrayerAccent
                    "Scripture Study" -> ScriptureAccent
                    else -> RoyalNavy
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = categoryColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = entry.category.ifBlank { "Reflection" },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = categoryColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.dateString,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp).testTag("journal_edit_${entry.id}")
                    ) {
                        Icon(
                            imageVector = CupertinoIcons.Pencil,
                            contentDescription = "Edit",
                            tint = RoyalNavy,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp).testTag("journal_delete_${entry.id}")
                    ) {
                        Icon(
                            imageVector = CupertinoIcons.Trash,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (entry.mood.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = CupertinoIcons.Sparkles,
                        contentDescription = null,
                        tint = ChurchGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Heart posture: ${entry.mood}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (entry.reflectionText.isNotBlank()) {
                Text(
                    text = entry.reflectionText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )
            }

            if (entry.prayerText.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PrayerAccent.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrayerAccent.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = CupertinoIcons.HandsSparkles,
                                contentDescription = null,
                                tint = PrayerAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Personal Prayer Point",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrayerAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = entry.prayerText,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Serif,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEditorDialog(
    entry: JournalEntryEntity?,
    onDismiss: () -> Unit,
    onSave: (title: String, reflection: String, prayer: String, category: String, mood: String, id: Int, devotionId: String) -> Unit
) {
    var title by remember { mutableStateOf(entry?.title ?: "") }
    var reflectionText by remember { mutableStateOf(entry?.reflectionText ?: "") }
    var prayerText by remember { mutableStateOf(entry?.prayerText ?: "") }
    var category by remember { mutableStateOf(entry?.category ?: "Devotion Reflection") }
    var mood by remember { mutableStateOf(entry?.mood ?: "Peaceful") }

    val categories = listOf("Devotion Reflection", "Gratitude", "Prayer", "Scripture Study", "Personal")
    val moods = listOf("Peaceful", "Grateful", "Joyful", "Seeking Guidance", "Humbled")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .testTag("journal_editor_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (entry != null) "Edit Journal Entry" else "New Spiritual Journal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavy
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = CupertinoIcons.Xmark, contentDescription = "Close")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title / Spiritual Focus") },
                            placeholder = { Text("e.g. Walking in Grace, Answered Prayer") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("journal_title_input")
                        )
                    }

                    // Category Selector
                    item {
                        Text(
                            text = "Category",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(categories) { cat ->
                                val selected = category == cat
                                Surface(
                                    onClick = { category = cat },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) RoyalNavy else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.testTag("journal_cat_option_$cat")
                                ) {
                                    Text(
                                        text = cat,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Mood / Heart Posture Selector
                    item {
                        Text(
                            text = "Heart Posture",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(moods) { m ->
                                val selected = mood == m
                                Surface(
                                    onClick = { mood = m },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) ChurchGold else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.testTag("journal_mood_option_$m")
                                ) {
                                    Text(
                                        text = m,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = reflectionText,
                            onValueChange = { reflectionText = it },
                            label = { Text("Spiritual Reflection & Insights") },
                            placeholder = { Text("What did God reveal to you through His Word, prayer, or today's walk?") },
                            minLines = 4,
                            maxLines = 8,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("journal_reflection_input")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = prayerText,
                            onValueChange = { prayerText = it },
                            label = { Text("Personal Prayer Point") },
                            placeholder = { Text("Write your prayer or thanksgiving to the Father...") },
                            minLines = 3,
                            maxLines = 6,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("journal_prayer_input")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank() || reflectionText.isNotBlank() || prayerText.isNotBlank()) {
                                onSave(
                                    title.ifBlank { "Sanctuary Reflection" },
                                    reflectionText,
                                    prayerText,
                                    category,
                                    mood,
                                    entry?.id ?: 0,
                                    entry?.devotionId ?: ""
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("journal_save_button")
                    ) {
                        Icon(imageVector = CupertinoIcons.Checkmark, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Entry")
                    }
                }
            }
        }
    }
}
