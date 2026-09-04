package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BibleBook
import com.example.data.model.BibleChapter
import com.example.data.model.BibleVerse
import com.example.data.repository.ChurchDataSeed
import com.example.ui.ChurchTab
import com.example.ui.ChurchViewModel
import com.example.ui.components.CupertinoIcons
import com.example.ui.components.DailyScriptureReaderComponent
import com.example.ui.components.IosGroupedCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch

data class PopularPassageSuggestion(
    val title: String,
    val bookName: String,
    val chapter: Int,
    val verse: Int,
    val tag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptureScreen(
    viewModel: ChurchViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var showPassagePickerSheet by remember { mutableStateOf(false) }
    var showBookmarksSheet by remember { mutableStateOf(false) }
    var showDailyVerseSheet by remember { mutableStateOf(false) }
    var showTextSizeSettings by remember { mutableStateOf(false) }
    var showTranslationMenu by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchKeyword by remember { mutableStateOf("") }
    var selectedVerseForAction by remember { mutableStateOf<BibleVerse?>(null) }

    val translations = listOf("NIV", "ESV", "KJV", "NLT")

    val currentBook = uiState.selectedBook
    val currentChapter = remember(currentBook, uiState.selectedChapterNumber) {
        ChurchDataSeed.getChapterForBook(currentBook, uiState.selectedChapterNumber)
    }

    // Popular Suggestions list for quick jumps
    val suggestions = remember {
        listOf(
            PopularPassageSuggestion("John 3:16", "John", 3, 16, "God's Love"),
            PopularPassageSuggestion("Psalms 23:1", "Psalms", 23, 1, "The Shepherd"),
            PopularPassageSuggestion("Romans 8:28", "Romans", 8, 28, "God's Purpose"),
            PopularPassageSuggestion("Philippians 4:6", "Philippians", 4, 6, "Peace in Prayer"),
            PopularPassageSuggestion("Matthew 5:3", "Matthew", 5, 3, "The Beatitudes"),
            PopularPassageSuggestion("Genesis 1:1", "Genesis", 1, 1, "Creation"),
            PopularPassageSuggestion("1 Cor 13:4", "1 Corinthians", 13, 4, "Love"),
            PopularPassageSuggestion("Proverbs 3:5", "Proverbs", 3, 5, "Trust"),
            PopularPassageSuggestion("Isaiah 40:31", "Isaiah", 40, 31, "Strength")
        )
    }

    // Filtered verses when searching
    val displayVerses = remember(searchKeyword, currentChapter) {
        if (searchKeyword.isBlank()) {
            currentChapter.verses
        } else {
            val results = mutableListOf<BibleVerse>()
            ChurchDataSeed.bibleBooks.forEach { book ->
                book.chapters.forEach { ch ->
                    ch.verses.forEach { verse ->
                        if (verse.text.contains(searchKeyword, ignoreCase = true) ||
                            verse.bookName.contains(searchKeyword, ignoreCase = true)
                        ) {
                            results.add(verse)
                        }
                    }
                }
            }
            results
        }
    }

    // Auto-scroll when specific verse is highlighted
    LaunchedEffect(uiState.selectedVerseNumber, displayVerses) {
        val targetVerse = uiState.selectedVerseNumber
        if (targetVerse != null && searchKeyword.isBlank()) {
            val targetIndex = displayVerses.indexOfFirst { it.verseNumber == targetVerse }
            if (targetIndex >= 0) {
                listState.animateScrollToItem((targetIndex + 1).coerceAtLeast(0))
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile avatar badge
                    IconButton(
                        onClick = { viewModel.selectTab(ChurchTab.PROFILE) },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("scripture_open_profile_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(RoyalNavy),
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

                    // Main Clean Passage Selector Pill (e.g. "Romans 8 ▼")
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showPassagePickerSheet = true }
                            .testTag("scripture_passage_picker_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Icon(
                                imageVector = CupertinoIcons.Book,
                                contentDescription = null,
                                tint = ScriptureAccent,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${currentBook.name} ${currentChapter.chapterNumber}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 130.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select Passage",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Top Action Icons (Search, Translation, Aa, Bookmarks)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Translation Pill
                        Box {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = RoyalNavy.copy(alpha = 0.08f),
                                border = BorderStroke(1.dp, RoyalNavy.copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showTranslationMenu = true }
                                    .testTag("scripture_translation_btn")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = uiState.selectedTranslation,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalNavy,
                                        fontSize = 11.sp
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = RoyalNavy,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showTranslationMenu,
                                onDismissRequest = { showTranslationMenu = false }
                            ) {
                                translations.forEach { trans ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = trans,
                                                    fontWeight = if (trans == uiState.selectedTranslation) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (trans == uiState.selectedTranslation) RoyalNavy else MaterialTheme.colorScheme.onSurface
                                                )
                                                if (trans == uiState.selectedTranslation) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = ChurchGold,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        },
                                        onClick = {
                                            viewModel.setTranslation(trans)
                                            showTranslationMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Search Toggle Button
                        IconButton(
                            onClick = { isSearchActive = !isSearchActive },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("scripture_search_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isSearchActive) Icons.Default.Close else CupertinoIcons.Search,
                                contentDescription = "Search Scripture",
                                tint = if (isSearchActive) RoyalNavy else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        // Reader Appearance (Aa)
                        IconButton(
                            onClick = { showTextSizeSettings = !showTextSizeSettings },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("scripture_font_size_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = "Adjust Font Size",
                                tint = if (showTextSizeSettings) RoyalNavy else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Daily Verse Reader Shortcut
                        IconButton(
                            onClick = { showDailyVerseSheet = true },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("scripture_daily_verse_button")
                        ) {
                            Icon(
                                imageVector = CupertinoIcons.Sparkles,
                                contentDescription = "Daily Scripture Reader",
                                tint = ChurchGold,
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        // Bookmarks Button with Badge
                        IconButton(
                            onClick = { showBookmarksSheet = true },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("scripture_bookmarks_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (bookmarks.isNotEmpty()) {
                                        Badge(
                                            containerColor = ChurchGold,
                                            contentColor = RoyalNavy
                                        ) {
                                            Text(bookmarks.size.toString(), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = CupertinoIcons.Bookmark,
                                    contentDescription = "Saved Bookmarks",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    }
                }

                // Collapsible Search Bar
                AnimatedVisibility(
                    visible = isSearchActive,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        OutlinedTextField(
                            value = searchKeyword,
                            onValueChange = { searchKeyword = it },
                            placeholder = { Text("Search verses, words (e.g. 'grace', 'peace')...", fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = CupertinoIcons.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = ScriptureAccent
                                )
                            },
                            trailingIcon = {
                                if (searchKeyword.isNotEmpty()) {
                                    IconButton(onClick = { searchKeyword = "" }) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                focusedBorderColor = RoyalNavy,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("scripture_keyword_search_input")
                        )
                    }
                }

                // Collapsible Quick Text Size Settings
                AnimatedVisibility(
                    visible = showTextSizeSettings,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "A",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Slider(
                                value = uiState.readerFontSizeSp,
                                onValueChange = { viewModel.setReaderFontSize(it) },
                                valueRange = 14f..26f,
                                steps = 5,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp)
                            )

                            Text(
                                text = "A",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "${uiState.readerFontSizeSp.toInt()} sp",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = RoyalNavy
                            )
                        }
                    }
                }

                // Subtle bottom border line
                Divider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                    thickness = 1.dp
                )
            }
        },
        bottomBar = {
            // Clean Bottom Floating Chapter Navigator Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Chapter Button
                    TextButton(
                        onClick = {
                            if (uiState.selectedChapterNumber > 1) {
                                viewModel.setScriptureChapter(uiState.selectedChapterNumber - 1)
                            } else {
                                val bookIndex = ChurchDataSeed.bibleBooks.indexOfFirst { it.name == currentBook.name }
                                if (bookIndex > 0) {
                                    val prevBook = ChurchDataSeed.bibleBooks[bookIndex - 1]
                                    viewModel.setScriptureBook(prevBook, prevBook.chapterCount)
                                }
                            }
                        },
                        modifier = Modifier.testTag("scripture_prev_chapter_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = RoyalNavy
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Previous",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = RoyalNavy
                        )
                    }

                    // Chapter indicator text
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showPassagePickerSheet = true }
                    ) {
                        Text(
                            text = "Ch ${currentChapter.chapterNumber} of ${currentBook.chapterCount}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    // Next Chapter Button
                    TextButton(
                        onClick = {
                            if (uiState.selectedChapterNumber < currentBook.chapterCount) {
                                viewModel.setScriptureChapter(uiState.selectedChapterNumber + 1)
                            } else {
                                val bookIndex = ChurchDataSeed.bibleBooks.indexOfFirst { it.name == currentBook.name }
                                if (bookIndex >= 0 && bookIndex < ChurchDataSeed.bibleBooks.size - 1) {
                                    val nextBook = ChurchDataSeed.bibleBooks[bookIndex + 1]
                                    viewModel.setScriptureBook(nextBook, 1)
                                }
                            }
                        },
                        modifier = Modifier.testTag("scripture_next_chapter_btn")
                    ) {
                        Text(
                            text = "Next",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = RoyalNavy
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = RoyalNavy
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        // Main Clean Scripture Reading Canvas
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Chapter Title Header
            if (searchKeyword.isBlank()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currentBook.name.uppercase(),
                            style = AppleTypographyStyles.referenceTag,
                            color = ScriptureAccent,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Chapter ${currentChapter.chapterNumber}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontFamily = FontFamily.Serif
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "${currentBook.testament} Testament • ${uiState.selectedTranslation}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Divider(
                            modifier = Modifier.width(60.dp),
                            color = ChurchGold,
                            thickness = 2.dp
                        )
                    }
                }
            } else {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ScriptureAccent.copy(alpha = 0.08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = CupertinoIcons.Search,
                                contentDescription = null,
                                tint = ScriptureAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Found ${displayVerses.size} verses containing \"$searchKeyword\"",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = ScriptureAccent
                            )
                        }
                    }
                }
            }

            if (displayVerses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No verses found for \"$searchKeyword\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Continuous, Clean Verse Stream
            itemsIndexed(displayVerses) { _, verse ->
                val isBookmarked = bookmarks.any {
                    it.book == verse.bookName && it.chapter == verse.chapter && it.verse == verse.verseNumber
                }
                val isTargetedVerse = uiState.selectedVerseNumber == verse.verseNumber
                val isSelectedForAction = selectedVerseForAction?.verseNumber == verse.verseNumber && selectedVerseForAction?.chapter == verse.chapter

                val containerColor by animateColorAsState(
                    targetValue = when {
                        isSelectedForAction -> ScriptureAccent.copy(alpha = 0.12f)
                        isTargetedVerse -> ChurchGold.copy(alpha = 0.14f)
                        isBookmarked -> ChurchGold.copy(alpha = 0.06f)
                        else -> Color.Transparent
                    },
                    animationSpec = tween(250),
                    label = "verse_container"
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = containerColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            selectedVerseForAction = if (selectedVerseForAction == verse) null else verse
                        }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .testTag("verse_item_${verse.bookName}_${verse.chapter}_${verse.verseNumber}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Minimal, elegant verse number badge
                            Box(
                                modifier = Modifier
                                    .padding(top = 3.dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isBookmarked -> ChurchGold
                                            isTargetedVerse -> RoyalNavy
                                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${verse.verseNumber}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isBookmarked || isTargetedVerse) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Verse Text with rich typography
                            Column(modifier = Modifier.weight(1f)) {
                                if (searchKeyword.isNotBlank()) {
                                    Text(
                                        text = "${verse.bookName} ${verse.chapter}:${verse.verseNumber}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ScriptureAccent,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                }

                                Text(
                                    text = verse.text,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = uiState.readerFontSizeSp.sp,
                                        lineHeight = (uiState.readerFontSizeSp * 1.55f).sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = FontFamily.Serif
                                )
                            }
                        }

                        // Compact inline action menu when a verse is tapped
                        AnimatedVisibility(
                            visible = isSelectedForAction,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Bookmark Action
                                TextButton(
                                    onClick = {
                                        viewModel.toggleBookmark(
                                            verse.bookName,
                                            verse.chapter,
                                            verse.verseNumber,
                                            verse.text
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isBookmarked) CupertinoIcons.BookmarkFill else CupertinoIcons.Bookmark,
                                        contentDescription = null,
                                        tint = if (isBookmarked) ChurchGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isBookmarked) "Bookmarked" else "Bookmark",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isBookmarked) ChurchGold else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Copy Action
                                TextButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText(
                                            "Scripture",
                                            "“${verse.text}” — ${verse.bookName} ${verse.chapter}:${verse.verseNumber} (${uiState.selectedTranslation})"
                                        )
                                        clipboard.setPrimaryClip(clip)
                                        viewModel.showToast("Verse copied to clipboard")
                                        selectedVerseForAction = null
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ContentCopy,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Copy",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Share Action
                                TextButton(
                                    onClick = {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "“${verse.text}” — ${verse.bookName} ${verse.chapter}:${verse.verseNumber} (${uiState.selectedTranslation})"
                                            )
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share Scripture"))
                                        selectedVerseForAction = null
                                    }
                                ) {
                                    Icon(
                                        imageVector = CupertinoIcons.Share,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Share",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Chapter Completion & Reading Goal Card
            if (searchKeyword.isBlank()) {
                item {
                    val chapterKey = "${currentBook.name} ${currentChapter.chapterNumber}"
                    val isChapterCompleted = uiState.completedChapters.contains(chapterKey)

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isChapterCompleted) Color(0xFF2E7D32).copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(
                            1.dp,
                            if (isChapterCompleted) Color(0xFF2E7D32).copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .testTag("scripture_chapter_completion_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (isChapterCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isChapterCompleted) Color(0xFF2E7D32) else ChurchGold,
                                modifier = Modifier.size(32.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (isChapterCompleted) "Chapter Completed! ✓" else "Finished reading this chapter?",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isChapterCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (isChapterCompleted)
                                    "Praise God! You completed ${currentBook.name} ${currentChapter.chapterNumber}."
                                else
                                    "Mark ${currentBook.name} ${currentChapter.chapterNumber} as completed to track your Bible reading journey.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.toggleChapterReadingCompleted(chapterKey)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isChapterCompleted) Color(0xFF2E7D32) else RoyalNavy
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("scripture_mark_chapter_completed_btn")
                                ) {
                                    Icon(
                                        imageVector = if (isChapterCompleted) Icons.Default.Check else Icons.Default.DoneAll,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isChapterCompleted) "Completed ✓" else "Mark as Read")
                                }

                                if (uiState.selectedChapterNumber < currentBook.chapterCount) {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.setScriptureChapter(uiState.selectedChapterNumber + 1)
                                        },
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Next Chapter")
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
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

    // Modern Modal Bottom Sheet for Passage Navigation (Clean, searchable, category-indexed)
    if (showPassagePickerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPassagePickerSheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            var selectedTab by remember { mutableIntStateOf(0) } // 0 = OT, 1 = NT, 2 = Direct Jump
            var pickerBookSearch by remember { mutableStateOf("") }
            var selectedBookForChapters by remember { mutableStateOf(currentBook) }
            var isSelectingChapters by remember { mutableStateOf(false) }

            // Direct Jump Input fields
            var directBookInput by remember { mutableStateOf(currentBook.name) }
            var directChapterInput by remember { mutableStateOf(uiState.selectedChapterNumber.toString()) }
            var directVerseInput by remember { mutableStateOf(uiState.selectedVerseNumber?.toString() ?: "") }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .navigationBarsPadding()
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSelectingChapters) "${selectedBookForChapters.name} Chapters" else "Select Passage",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    if (isSelectingChapters) {
                        TextButton(onClick = { isSelectingChapters = false }) {
                            Text("← Back to Books", color = RoyalNavy, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        IconButton(onClick = { showPassagePickerSheet = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (!isSelectingChapters) {
                    // Quick Filter / Segmented Tab (Old Testament / New Testament / Direct Jump)
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Old Testament", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("New Testament", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("Direct Jump", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (selectedTab == 0 || selectedTab == 1) {
                        // Search bar for books
                        OutlinedTextField(
                            value = pickerBookSearch,
                            onValueChange = { pickerBookSearch = it },
                            placeholder = { Text("Search books...", fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(imageVector = CupertinoIcons.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                focusedBorderColor = RoyalNavy
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )

                        val currentTestamentFilter = if (selectedTab == 0) "Old Testament" else "New Testament"
                        val booksToShow = remember(pickerBookSearch, currentTestamentFilter) {
                            ChurchDataSeed.allBibleBooksMetadata.filter {
                                it.testament == currentTestamentFilter &&
                                    (pickerBookSearch.isBlank() || it.name.contains(pickerBookSearch, ignoreCase = true))
                            }
                        }

                        // Grid of Books
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 340.dp)
                        ) {
                            items(booksToShow) { book ->
                                val isSelected = currentBook.name == book.name
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) RoyalNavy else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) ChurchGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            selectedBookForChapters = book
                                            isSelectingChapters = true
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = book.name,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${book.chapterCount} ch",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) ChurchGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Direct Jump 3-field input screen
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "Enter Book, Chapter, and optional Verse:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = directBookInput,
                                    onValueChange = { directBookInput = it },
                                    label = { Text("Book") },
                                    placeholder = { Text("John") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(2f)
                                        .testTag("scripture_input_book")
                                )

                                OutlinedTextField(
                                    value = directChapterInput,
                                    onValueChange = { directChapterInput = it.filter { c -> c.isDigit() } },
                                    label = { Text("Ch") },
                                    placeholder = { Text("1") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("scripture_input_chapter")
                                )

                                OutlinedTextField(
                                    value = directVerseInput,
                                    onValueChange = { directVerseInput = it.filter { c -> c.isDigit() } },
                                    label = { Text("Verse") },
                                    placeholder = { Text("All") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("scripture_input_verse")
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val matchedBook = ChurchDataSeed.findBookByName(directBookInput) ?: currentBook
                                    val ch = directChapterInput.toIntOrNull() ?: 1
                                    val v = directVerseInput.toIntOrNull()
                                    viewModel.setScriptureBook(matchedBook, ch, v)
                                    showPassagePickerSheet = false
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("scripture_go_button")
                            ) {
                                Text("Jump to Passage", fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Suggestions list
                            Text(
                                text = "Popular Passages:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(suggestions) { s ->
                                    SuggestionChip(
                                        onClick = {
                                            val b = ChurchDataSeed.findBookByName(s.bookName) ?: currentBook
                                            viewModel.setScriptureBook(b, s.chapter, s.verse)
                                            showPassagePickerSheet = false
                                        },
                                        label = { Text(s.title, fontWeight = FontWeight.SemiBold) },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.testTag("suggestion_chip_${s.title.replace(" ", "_")}")
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Chapter Grid Picker for Selected Book
                    val chapterCount = selectedBookForChapters.chapterCount.coerceAtLeast(1)
                    val chaptersList = (1..chapterCount).toList()

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 340.dp)
                    ) {
                        items(chaptersList) { ch ->
                            val isCurrent = currentBook.name == selectedBookForChapters.name && uiState.selectedChapterNumber == ch
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isCurrent) RoyalNavy else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isCurrent) ChurchGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        viewModel.setScriptureBook(selectedBookForChapters, ch)
                                        showPassagePickerSheet = false
                                    }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$ch",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Bookmarks Modal Bottom Sheet
    if (showBookmarksSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBookmarksSheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Saved Bookmarks (${bookmarks.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { showBookmarksSheet = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (bookmarks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = CupertinoIcons.Bookmark,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No saved bookmarks yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Tap any verse in the reader to bookmark it.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxHeight(0.65f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(bookmarks) { b ->
                            IosGroupedCard(
                                onClick = {
                                    val bBook = ChurchDataSeed.findBookByName(b.book)
                                    if (bBook != null) {
                                        viewModel.setScriptureBook(bBook, b.chapter, b.verse)
                                        showBookmarksSheet = false
                                    }
                                }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${b.book} ${b.chapter}:${b.verse} (${b.translation})",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = ChurchGold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "\"${b.text}\"",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            viewModel.toggleBookmark(b.book, b.chapter, b.verse, b.text)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Remove Bookmark",
                                            tint = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Daily Verse Reader Modal Sheet
        if (showDailyVerseSheet) {
            ModalBottomSheet(
                onDismissRequest = { showDailyVerseSheet = false },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 32.dp)
                ) {
                    DailyScriptureReaderComponent(
                        viewModel = viewModel,
                        onNavigateToChapter = { bookName, chapter, verse ->
                            showDailyVerseSheet = false
                            val foundBook = ChurchDataSeed.findBookByName(bookName) ?: currentBook
                            viewModel.setScriptureBook(foundBook, chapter, verse)
                            coroutineScope.launch {
                                listState.animateScrollToItem((verse - 1).coerceAtLeast(0))
                            }
                        }
                    )
                }
            }
        }
    }
}
