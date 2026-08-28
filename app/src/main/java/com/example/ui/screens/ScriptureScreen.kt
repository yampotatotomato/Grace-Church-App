package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BibleBook
import com.example.data.model.BibleChapter
import com.example.data.repository.ChurchDataSeed
import com.example.ui.ChurchViewModel
import com.example.ui.components.IosGroupedCard
import com.example.ui.components.IosSegmentedControl
import com.example.ui.components.IosTopBar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptureScreen(
    viewModel: ChurchViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()

    var showBookSelector by remember { mutableStateOf(false) }
    var showBookmarksSheet by remember { mutableStateOf(false) }
    var showTextSizeSettings by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchKeyword by remember { mutableStateOf("") }

    val translations = listOf("NIV", "ESV", "KJV", "NLT")
    val selectedTranslationIndex = translations.indexOf(uiState.selectedTranslation).coerceAtLeast(0)

    val currentBook = uiState.selectedBook
    val currentChapter = currentBook.chapters.find { it.chapterNumber == uiState.selectedChapterNumber }
        ?: currentBook.chapters.firstOrNull() ?: BibleChapter(currentBook.name, 1, emptyList())

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

        // Book and Chapter Selector Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Book & Chapter dropdown button
            Surface(
                onClick = { showBookSelector = true },
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier.testTag("scripture_book_selector_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = ScriptureAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${currentBook.name} ${currentChapter.chapterNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quick Chapter Stepper
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            ) {
                IconButton(
                    onClick = {
                        val prevChapter = currentBook.chapters.find { it.chapterNumber < currentChapter.chapterNumber }
                        if (prevChapter != null) {
                            viewModel.setScriptureChapter(prevChapter.chapterNumber)
                        }
                    },
                    modifier = Modifier.size(36.dp),
                    enabled = currentBook.chapters.any { it.chapterNumber < currentChapter.chapterNumber }
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous Chapter",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Ch ${currentChapter.chapterNumber}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                IconButton(
                    onClick = {
                        val nextChapter = currentBook.chapters.find { it.chapterNumber > currentChapter.chapterNumber }
                        if (nextChapter != null) {
                            viewModel.setScriptureChapter(nextChapter.chapterNumber)
                        }
                    },
                    modifier = Modifier.size(36.dp),
                    enabled = currentBook.chapters.any { it.chapterNumber > currentChapter.chapterNumber }
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next Chapter",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Translation Selector (iOS Segmented Control)
        IosSegmentedControl(
            items = translations,
            selectedIndex = selectedTranslationIndex,
            onSelectedIndexChanged = { viewModel.setTranslation(translations[it]) },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
        )

        // Accessibility Font Size Slider (Collapsible)
        AnimatedVisibility(visible = showTextSizeSettings) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Accessibility: Reading Text Size",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${uiState.readerFontSizeSp.toInt()} sp",
                            style = MaterialTheme.typography.labelMedium,
                            color = IosBlue,
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

        // Scripture Search Input
        OutlinedTextField(
            value = searchKeyword,
            onValueChange = { searchKeyword = it },
            placeholder = { Text("Search verses or topics (e.g., 'love', 'peace', 'refuge')") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search Scripture")
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
                focusedBorderColor = IosBlue
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .testTag("scripture_search_input")
        )

        // Main Scripture Text Flow
        val displayVerses = if (searchKeyword.isBlank()) {
            currentChapter.verses
        } else {
            // Search across current book and others
            val results = mutableListOf<com.example.data.model.BibleVerse>()
            ChurchDataSeed.bibleBooks.forEach { book ->
                book.chapters.forEach { chapter ->
                    chapter.verses.forEach { verse ->
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                            .padding(bottom = 6.dp),
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

            items(displayVerses) { verse ->
                val isBookmarked = bookmarks.any {
                    it.book == verse.bookName && it.chapter == verse.chapter && it.verse == verse.verseNumber
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isBookmarked) ChurchGold.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isBookmarked) ChurchGold.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
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
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (isBookmarked) ChurchGold else RoyalNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${verse.verseNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            if (searchKeyword.isNotBlank()) {
                                Text(
                                    text = "${verse.bookName} ${verse.chapter}:${verse.verseNumber}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ScriptureAccent
                                )
                                Spacer(modifier = Modifier.height(4.dp))
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

    // Modal Book & Chapter Selector
    if (showBookSelector) {
        ModalBottomSheet(
            onDismissRequest = { showBookSelector = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Select Book of the Bible",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxHeight(0.65f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(ChurchDataSeed.bibleBooks) { book ->
                        val isSelected = book.id == currentBook.id
                        Surface(
                            onClick = {
                                viewModel.setScriptureBook(book)
                                showBookSelector = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) IosBlueLight else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = book.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) IosBlue else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${book.testament} • ${book.category}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = IosBlue
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Bookmarks Sheet
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
                                    val bBook = ChurchDataSeed.bibleBooks.find { it.name == b.book }
                                    if (bBook != null) {
                                        viewModel.setScriptureBook(bBook, b.chapter)
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
