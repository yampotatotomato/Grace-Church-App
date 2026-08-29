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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
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
import com.example.ui.components.IosGroupedCard
import com.example.ui.components.IosSegmentedControl
import com.example.ui.components.IosTopBar
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
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var showBookmarksSheet by remember { mutableStateOf(false) }
    var showTextSizeSettings by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchKeyword by remember { mutableStateOf("") }

    // Translations list
    val translations = listOf("NIV", "ESV", "KJV", "NLT")
    val selectedTranslationIndex = translations.indexOf(uiState.selectedTranslation).coerceAtLeast(0)

    val currentBook = uiState.selectedBook
    val currentChapter = remember(currentBook, uiState.selectedChapterNumber) {
        ChurchDataSeed.getChapterForBook(currentBook, uiState.selectedChapterNumber)
    }

    // 3 Input Fields State
    var bookInputText by remember { mutableStateOf(currentBook.name) }
    var chapterInputText by remember { mutableStateOf(uiState.selectedChapterNumber.toString()) }
    var verseInputText by remember { mutableStateOf(uiState.selectedVerseNumber?.toString() ?: "") }
    var isBookDropdownOpen by remember { mutableStateOf(false) }

    // Keep inputs in sync when UI state updates from external sources (e.g. bookmarks or suggestions)
    LaunchedEffect(currentBook.name) {
        bookInputText = currentBook.name
    }
    LaunchedEffect(uiState.selectedChapterNumber) {
        chapterInputText = uiState.selectedChapterNumber.toString()
    }
    LaunchedEffect(uiState.selectedVerseNumber) {
        verseInputText = uiState.selectedVerseNumber?.toString() ?: ""
    }

    // Autocomplete filtered books
    val filteredBooks = remember(bookInputText) {
        if (bookInputText.isBlank()) {
            ChurchDataSeed.allBibleBooksMetadata
        } else {
            val query = bookInputText.trim().lowercase()
            ChurchDataSeed.allBibleBooksMetadata.filter {
                it.name.lowercase().contains(query) || it.category.lowercase().contains(query)
            }
        }
    }

    // Popular Suggestions list
    val suggestions = remember {
        listOf(
            PopularPassageSuggestion("John 3:16", "John", 3, 16, "God's Love"),
            PopularPassageSuggestion("Psalms 23:1", "Psalms", 23, 1, "The Shepherd"),
            PopularPassageSuggestion("Romans 8:28", "Romans", 8, 28, "God's Purpose"),
            PopularPassageSuggestion("Philippians 4:6", "Philippians", 4, 6, "Peace in Prayer"),
            PopularPassageSuggestion("Matthew 5:3", "Matthew", 5, 3, "The Beatitudes"),
            PopularPassageSuggestion("Genesis 1:1", "Genesis", 1, 1, "In the Beginning"),
            PopularPassageSuggestion("Ephesians 2:8", "Ephesians", 2, 8, "Saved by Grace"),
            PopularPassageSuggestion("1 Cor 13:4", "1 Corinthians", 13, 4, "Love is Patient"),
            PopularPassageSuggestion("Proverbs 3:5", "Proverbs", 3, 5, "Trust the Lord"),
            PopularPassageSuggestion("Isaiah 40:31", "Isaiah", 40, 31, "Renew Strength"),
            PopularPassageSuggestion("Revelation 21:4", "Revelation", 21, 4, "No More Tears"),
            PopularPassageSuggestion("Joshua 1:9", "Joshua", 1, 9, "Strong & Courageous"),
            PopularPassageSuggestion("Jeremiah 29:11", "Jeremiah", 29, 11, "Hope & Future"),
            PopularPassageSuggestion("Hebrews 11:1", "Hebrews", 11, 1, "Living Faith")
        )
    }

    // Function to apply navigation
    fun applyPassageNavigation(book: BibleBook? = null, chapter: Int? = null, verse: Int? = null) {
        focusManager.clearFocus()
        isBookDropdownOpen = false

        val targetBook = book ?: ChurchDataSeed.findBookByName(bookInputText) ?: currentBook
        val chNum = chapter ?: chapterInputText.toIntOrNull() ?: 1
        val vNum = verse ?: verseInputText.toIntOrNull()

        viewModel.setScriptureBook(targetBook, chNum, vNum)
        bookInputText = targetBook.name
        chapterInputText = chNum.coerceIn(1, targetBook.chapterCount.coerceAtLeast(1)).toString()
        if (vNum != null) {
            verseInputText = vNum.toString()
        }
    }

    // Main Scripture Text Flow
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

    // Scroll to selected verse when targeted
    LaunchedEffect(uiState.selectedVerseNumber, displayVerses) {
        val targetVerse = uiState.selectedVerseNumber
        if (targetVerse != null && searchKeyword.isBlank()) {
            val targetIndex = displayVerses.indexOfFirst { it.verseNumber == targetVerse }
            if (targetIndex >= 0) {
                // Offset + 2 accounts for top header items in LazyColumn
                listState.animateScrollToItem((targetIndex + 1).coerceAtLeast(0))
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        IosTopBar(
            title = "Holy Scripture",
            subtitle = "BIBLE READER",
            actions = {
                // Profile View Switcher
                IconButton(
                    onClick = { viewModel.selectTab(ChurchTab.PROFILE) },
                    modifier = Modifier.testTag("scripture_open_profile_button")
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

                // Search toggle
                IconButton(
                    onClick = { isSearchActive = !isSearchActive },
                    modifier = Modifier.testTag("scripture_search_toggle_button")
                ) {
                    Icon(
                        imageVector = CupertinoIcons.Search,
                        contentDescription = "Search Scripture",
                        tint = if (isSearchActive) RoyalNavy else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Bookmarks drawer button
                IconButton(
                    onClick = { showBookmarksSheet = true },
                    modifier = Modifier.testTag("scripture_bookmarks_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (bookmarks.isNotEmpty()) {
                                Badge { Text(bookmarks.size.toString()) }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmarks,
                            contentDescription = "Saved Bookmarks",
                            tint = RoyalNavy
                        )
                    }
                }

                // Text Size Accessibility
                IconButton(
                    onClick = { showTextSizeSettings = !showTextSizeSettings },
                    modifier = Modifier.testTag("scripture_font_size_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = "Adjust Font Size",
                        tint = RoyalNavy
                    )
                }
            }
        )

        // 1. Translation Selector (Versions of the Bible)
        IosSegmentedControl(
            items = translations,
            selectedIndex = selectedTranslationIndex,
            onSelectedIndexChanged = { viewModel.setTranslation(translations[it]) },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
        )

        // 2. 3 INPUT FIELDS (Book, Chapter, Verse) - Placed directly BELOW the versions of the Bible
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .shadow(4.dp, RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Header row for Navigator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(ScriptureAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = CupertinoIcons.Book,
                                contentDescription = null,
                                tint = ScriptureAccent,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PASSAGE NAVIGATOR",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ScriptureAccent,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = "${currentBook.testament} • ${currentBook.category}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // The 3 Input Fields in a neat, responsive Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Field 1: Book of the Bible (with Autocomplete Dropdown)
                    Box(modifier = Modifier.weight(2.0f)) {
                        OutlinedTextField(
                            value = bookInputText,
                            onValueChange = {
                                bookInputText = it
                                isBookDropdownOpen = true
                            },
                            label = { Text("Book", fontSize = 12.sp) },
                            placeholder = { Text("e.g. John") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = CupertinoIcons.Book,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = ScriptureAccent
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { isBookDropdownOpen = !isBookDropdownOpen },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isBookDropdownOpen) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                        contentDescription = "Show books",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                focusedBorderColor = RoyalNavy,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    applyPassageNavigation()
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged {
                                    if (it.isFocused) {
                                        isBookDropdownOpen = true
                                    }
                                }
                                .testTag("scripture_input_book")
                        )

                        // Autocomplete Dropdown Menu for Book selection
                        DropdownMenu(
                            expanded = isBookDropdownOpen && filteredBooks.isNotEmpty(),
                            onDismissRequest = { isBookDropdownOpen = false },
                            modifier = Modifier
                                .widthIn(min = 220.dp, max = 300.dp)
                                .heightIn(max = 280.dp)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            filteredBooks.take(15).forEach { b ->
                                val isCurrent = b.name.equals(currentBook.name, ignoreCase = true)
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = b.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isCurrent) RoyalNavy else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${b.testament} • ${b.chapterCount} ch",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 10.sp
                                            )
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (isCurrent) Icons.Default.Check else CupertinoIcons.Book,
                                            contentDescription = null,
                                            tint = if (isCurrent) ChurchGold else MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    onClick = {
                                        bookInputText = b.name
                                        isBookDropdownOpen = false
                                        applyPassageNavigation(book = b, chapter = 1, verse = null)
                                    },
                                    modifier = Modifier.testTag("book_suggestion_${b.name}")
                                )
                            }
                        }
                    }

                    // Field 2: Chapter Input
                    OutlinedTextField(
                        value = chapterInputText,
                        onValueChange = { input ->
                            val digitsOnly = input.filter { it.isDigit() }
                            chapterInputText = digitsOnly
                            if (digitsOnly.isNotBlank()) {
                                val ch = digitsOnly.toIntOrNull()
                                if (ch != null && ch in 1..currentBook.chapterCount) {
                                    viewModel.setScriptureChapter(ch, verseInputText.toIntOrNull())
                                }
                            }
                        },
                        label = { Text("Ch", fontSize = 12.sp) },
                        placeholder = { Text("1") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            focusedBorderColor = RoyalNavy,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                applyPassageNavigation()
                            }
                        ),
                        modifier = Modifier
                            .weight(1.0f)
                            .testTag("scripture_input_chapter")
                    )

                    // Field 3: Verse Input
                    OutlinedTextField(
                        value = verseInputText,
                        onValueChange = { input ->
                            val digitsOnly = input.filter { it.isDigit() }
                            verseInputText = digitsOnly
                            val vNum = digitsOnly.toIntOrNull()
                            viewModel.setScriptureVerse(vNum)
                        },
                        label = { Text("Verse", fontSize = 12.sp) },
                        placeholder = { Text("All") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            focusedBorderColor = RoyalNavy,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                applyPassageNavigation()
                            }
                        ),
                        modifier = Modifier
                            .weight(1.1f)
                            .testTag("scripture_input_verse")
                    )

                    // Go / Jump Action Button
                    Button(
                        onClick = { applyPassageNavigation() },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoyalNavy,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .height(54.dp)
                            .testTag("scripture_go_button")
                    ) {
                        Icon(
                            imageVector = CupertinoIcons.ArrowRight,
                            contentDescription = "Go to Passage",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Suggestions Section (Horizontal scroll chips for popular scriptures)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Suggestions:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(suggestions) { s ->
                            SuggestionChip(
                                onClick = {
                                    applyPassageNavigation(
                                        book = ChurchDataSeed.findBookByName(s.bookName),
                                        chapter = s.chapter,
                                        verse = s.verse
                                    )
                                },
                                label = {
                                    Text(
                                        text = s.title,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (currentBook.name == s.bookName && uiState.selectedChapterNumber == s.chapter && uiState.selectedVerseNumber == s.verse) {
                                        ChurchGold.copy(alpha = 0.2f)
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                    },
                                    labelColor = if (currentBook.name == s.bookName && uiState.selectedChapterNumber == s.chapter && uiState.selectedVerseNumber == s.verse) {
                                        RoyalNavy
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                ),
                                border = SuggestionChipDefaults.suggestionChipBorder(
                                    enabled = true,
                                    borderColor = if (currentBook.name == s.bookName && uiState.selectedChapterNumber == s.chapter && uiState.selectedVerseNumber == s.verse) {
                                        ChurchGold
                                    } else {
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    }
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("suggestion_chip_${s.title.replace(" ", "_")}")
                            )
                        }
                    }
                }
            }
        }

        // Search Bar (Collapsible)
        AnimatedVisibility(visible = isSearchActive) {
            OutlinedTextField(
                value = searchKeyword,
                onValueChange = { searchKeyword = it },
                placeholder = { Text("Search keyword across verses (e.g. 'peace', 'refuge')") },
                leadingIcon = {
                    Icon(imageVector = CupertinoIcons.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchKeyword.isNotEmpty()) {
                        IconButton(onClick = { searchKeyword = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    focusedBorderColor = RoyalNavy
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("scripture_keyword_search_input")
            )
        }

        // Accessibility Font Size Slider (Collapsible)
        AnimatedVisibility(visible = showTextSizeSettings) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reading Text Size",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${uiState.readerFontSizeSp.toInt()} sp",
                            style = MaterialTheme.typography.labelMedium,
                            color = RoyalNavy,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = uiState.readerFontSizeSp,
                        onValueChange = { viewModel.setReaderFontSize(it) },
                        valueRange = 14f..26f,
                        steps = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Quick Chapter Stepper Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = {
                    if (uiState.selectedChapterNumber > 1) {
                        viewModel.setScriptureChapter(uiState.selectedChapterNumber - 1)
                    }
                },
                enabled = uiState.selectedChapterNumber > 1,
                modifier = Modifier.testTag("scripture_prev_chapter_btn")
            ) {
                Icon(
                    imageVector = CupertinoIcons.ChevronLeft,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Previous Chapter")
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "${currentBook.name} ${currentChapter.chapterNumber} of ${currentBook.chapterCount}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            TextButton(
                onClick = {
                    if (uiState.selectedChapterNumber < currentBook.chapterCount) {
                        viewModel.setScriptureChapter(uiState.selectedChapterNumber + 1)
                    }
                },
                enabled = uiState.selectedChapterNumber < currentBook.chapterCount,
                modifier = Modifier.testTag("scripture_next_chapter_btn")
            ) {
                Text("Next Chapter")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = CupertinoIcons.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Main Scripture Text Flow (LazyColumn)
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (searchKeyword.isNotBlank()) {
                item {
                    Text(
                        text = "Found ${displayVerses.size} verses matching \"$searchKeyword\"",
                        style = MaterialTheme.typography.labelMedium,
                        color = ScriptureAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${currentBook.name} Chapter ${currentChapter.chapterNumber}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${currentBook.testament} • ${uiState.selectedTranslation}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (displayVerses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
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

            itemsIndexed(displayVerses) { _, verse ->
                val isBookmarked = bookmarks.any {
                    it.book == verse.bookName && it.chapter == verse.chapter && it.verse == verse.verseNumber
                }
                val isTargetedVerse = uiState.selectedVerseNumber == verse.verseNumber

                val targetBorderColor by animateColorAsState(
                    targetValue = when {
                        isTargetedVerse -> ChurchGold
                        isBookmarked -> ChurchGold.copy(alpha = 0.5f)
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    },
                    animationSpec = tween(300),
                    label = "border_color"
                )

                val targetContainerColor by animateColorAsState(
                    targetValue = when {
                        isTargetedVerse -> ChurchGold.copy(alpha = 0.14f)
                        isBookmarked -> ChurchGold.copy(alpha = 0.07f)
                        else -> MaterialTheme.colorScheme.surface
                    },
                    animationSpec = tween(300),
                    label = "container_color"
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = targetContainerColor,
                    border = androidx.compose.foundation.BorderStroke(
                        if (isTargetedVerse) 2.dp else 1.dp,
                        targetBorderColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.toggleBookmark(
                                verse.bookName,
                                verse.chapter,
                                verse.verseNumber,
                                verse.text
                            )
                        }
                        .testTag("verse_item_${verse.bookName}_${verse.chapter}_${verse.verseNumber}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isTargetedVerse -> ChurchGold
                                        isBookmarked -> ChurchGold
                                        else -> RoyalNavy
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${verse.verseNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            if (searchKeyword.isNotBlank()) {
                                Text(
                                    text = "${verse.bookName} ${verse.chapter}:${verse.verseNumber}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ScriptureAccent
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            } else if (isTargetedVerse) {
                                Text(
                                    text = "★ Highlighted Verse",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalNavy,
                                    fontSize = 10.sp
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

                        IconButton(
                            onClick = {
                                viewModel.toggleBookmark(
                                    verse.bookName,
                                    verse.chapter,
                                    verse.verseNumber,
                                    verse.text
                                )
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = if (isBookmarked) "Remove Bookmark" else "Bookmark Verse",
                                tint = if (isBookmarked) ChurchGold else MaterialTheme.colorScheme.outline
                            )
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
            containerColor = MaterialTheme.colorScheme.surface
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
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(48.dp)
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
    }
}
