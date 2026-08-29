package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.model.*
import com.example.data.repository.ChurchDataSeed
import com.example.data.repository.ChurchRepository
import com.example.notifications.NotificationHelper
import com.example.ui.theme.AccentTheme
import com.example.ui.theme.FontPreset
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ChurchTab(val title: String, val testTag: String) {
    HOME("Today", "tab_home"),
    SCRIPTURE("Scripture", "tab_scripture"),
    DEVOTION("Devotion", "tab_devotion"),
    JOURNAL("Journal", "tab_journal"),
    COMMUNITY("Community", "tab_community"),
    SERMONS("Sermons", "tab_sermons")
}

data class ChurchUiState(
    val isOnboardingCompleted: Boolean = false,
    val selectedTab: ChurchTab = ChurchTab.HOME,
    // Scripture state
    val selectedBook: BibleBook = ChurchDataSeed.bibleBooks.first(),
    val selectedChapterNumber: Int = 5,
    val selectedVerseNumber: Int? = null,
    val selectedTranslation: String = "NIV",
    val readerFontSizeSp: Float = 17f,
    val scriptureSearchQuery: String = "",
    // Audio Player State
    val activeSermon: Sermon? = ChurchDataSeed.sermons.first(),
    val isAudioPlaying: Boolean = false,
    val audioProgress: Float = 0.25f,
    val audioSpeed: Float = 1.0f,
    // Devotion State
    val selectedDevotional: Devotional = ChurchDataSeed.devotionals.first(),
    val currentDevotionJournal: JournalEntryEntity? = null,
    val devotionStreakDays: Int = 7,
    val favoriteDevotionIds: List<String> = emptyList(),
    val devotionsFilterFavoritesOnly: Boolean = false,
    // Journaling State
    val journalSearchQuery: String = "",
    val selectedJournalCategory: String = "All",
    val isShowingNewJournalModal: Boolean = false,
    val editingJournalEntry: JournalEntryEntity? = null,
    // Community / Prayer State
    val selectedAreaFilter: String = "All Areas",
    val isShowingPrayerModal: Boolean = false,
    val isShowingPastorContactModal: Boolean = false,
    val selectedPastorForContact: Pastor? = null,
    // Pastor Directory & Guidance Messaging State
    val pastorSearchQuery: String = "",
    val selectedPastorSpecialtyFilter: String = "All Pastors",
    val selectedPastorForProfile: Pastor? = null,
    val isShowingPastorProfileModal: Boolean = false,
    val isShowingGuidanceComposerModal: Boolean = false,
    val prefillGuidancePrompt: String = "",
    val prefillGuidanceCategory: String = "Spiritual Guidance & Discernment",
    // Settings, Themes & Notifications
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val accentTheme: AccentTheme = AccentTheme.GOLD_NAVY,
    val fontPreset: FontPreset = FontPreset.APPLE_BALANCED,
    val dailyVerseNotificationEnabled: Boolean = true,
    val dailyVerseTime: String = "07:00 AM",
    val meetingReminderEnabled: Boolean = true,
    val isNotificationSettingsOpen: Boolean = false,
    val isOnboardingReviewOpen: Boolean = false,
    val userToastMessage: String? = null
)

class ChurchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ChurchRepository.getInstance(application)
    private val prefs = application.getSharedPreferences("church_app_preferences", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        ChurchUiState(
            isOnboardingCompleted = prefs.getBoolean("key_onboarding_completed", false),
            themeMode = try {
                ThemeMode.valueOf(prefs.getString("key_theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name)
            } catch (e: Exception) {
                ThemeMode.SYSTEM
            },
            accentTheme = try {
                AccentTheme.valueOf(prefs.getString("key_accent_theme", AccentTheme.GOLD_NAVY.name) ?: AccentTheme.GOLD_NAVY.name)
            } catch (e: Exception) {
                AccentTheme.GOLD_NAVY
            },
            fontPreset = try {
                FontPreset.valueOf(prefs.getString("key_font_preset", FontPreset.APPLE_BALANCED.name) ?: FontPreset.APPLE_BALANCED.name)
            } catch (e: Exception) {
                FontPreset.APPLE_BALANCED
            },
            readerFontSizeSp = prefs.getFloat("key_reader_font_size", 17f),
            selectedTranslation = prefs.getString("key_bible_translation", "NIV") ?: "NIV",
            dailyVerseNotificationEnabled = prefs.getBoolean("key_daily_verse_notif", true),
            dailyVerseTime = prefs.getString("key_daily_verse_time", "07:00 AM") ?: "07:00 AM",
            meetingReminderEnabled = prefs.getBoolean("key_meeting_reminder_notif", true)
        )
    )
    val uiState: StateFlow<ChurchUiState> = _uiState.asStateFlow()

    // Data streams from repository
    val prayerRequests: StateFlow<List<PrayerRequestEntity>> = repository.allPrayerRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val journals: StateFlow<List<JournalEntryEntity>> = repository.allJournals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val joinedGroups: StateFlow<List<String>> = repository.joinedGroupIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pastorMessages: StateFlow<List<PastorMessageEntity>> = repository.allPastorMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteDevotionIds: StateFlow<List<String>> = repository.favoriteDevotionIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var audioPlaybackJob: Job? = null

    init {
        NotificationHelper.createNotificationChannels(application)
        observeDevotionJournal(_uiState.value.selectedDevotional.id)
        viewModelScope.launch {
            repository.favoriteDevotionIds.collect { favs ->
                _uiState.update { it.copy(favoriteDevotionIds = favs) }
            }
        }
        viewModelScope.launch {
            repository.allPastorMessages.collect { list ->
                if (list.isEmpty()) {
                    // Seed initial guidance exchanges
                    seedInitialGuidanceMessages()
                }
            }
        }
    }

    private suspend fun seedInitialGuidanceMessages() {
        val pastorSarah = ChurchDataSeed.pastors.find { it.id == "pastor_sarah" } ?: ChurchDataSeed.pastors.first()
        val pastorDavid = ChurchDataSeed.pastors.find { it.id == "pastor_david" } ?: ChurchDataSeed.pastors.first()

        val id1 = repository.sendPastorMessage(
            pastorId = pastorSarah.id,
            pastorName = pastorSarah.name,
            pastorTitle = pastorSarah.title,
            senderName = "Brother Thomas",
            senderEmail = "thomas@gracechurch.org",
            messageType = "Spiritual Guidance & Discernment",
            urgency = "Standard",
            subject = "Seeking peace during career transition",
            content = "Dear Pastor Sarah, I have been feeling deep anxiety about a new job offer that requires relocating our family. Could you share biblical guidance and pray with us as we seek God's direction?"
        )
        repository.providePastorReply(
            messageId = id1.toInt(),
            reply = "Dear brother Thomas, thank you for reaching out. Transition seasons are often where God grows our faith deepest. Remember that God does not ask you to figure out all ten steps ahead today—He only asks you to trust His hand for the step directly in front of you. Take quiet time with your family reading Psalm 32:8 this evening. I have placed your family on my daily prayer list, and our pastoral counseling doors are open if you'd like to schedule a dedicated session this week.",
            scriptureGuidance = "I will instruct you and teach you in the way you should go; I will counsel you with my loving eye on you. — Psalm 32:8",
            status = "Guidance Provided"
        )

        val id2 = repository.sendPastorMessage(
            pastorId = pastorDavid.id,
            pastorName = pastorDavid.name,
            pastorTitle = pastorDavid.title,
            senderName = "Sister Grace",
            senderEmail = "grace@gracechurch.org",
            messageType = "Biblical Counseling",
            urgency = "Standard",
            subject = "Encouraging a grieving friend with scripture",
            content = "Pastor David, your sermon on Sunday about God's comfort really resonated with me. How can I best encourage a colleague walking through the sudden loss of a parent without sounding clichéd?"
        )
        repository.providePastorReply(
            messageId = id2.toInt(),
            reply = "Grace and peace to you, Sister Grace. It is a holy calling to weep with those who weep. Often, our gentle presence, a listening ear, and warm home-cooked meals communicate Christ's compassion far more than fast theological explanations. When the moment is right, share 2 Corinthians 1:3-4 and let them know our church family is holding them in prayer.",
            scriptureGuidance = "Praise be to the God and Father of our Lord Jesus Christ, the Father of compassion and the God of all comfort, who comforts us in all our troubles. — 2 Corinthians 1:3-4",
            status = "Guidance Provided"
        )
    }

    private fun observeDevotionJournal(devotionId: String) {
        viewModelScope.launch {
            repository.getJournalForDevotion(devotionId).collect { entry ->
                _uiState.update { it.copy(currentDevotionJournal = entry) }
            }
        }
    }

    fun completeOnboarding(openTabAfter: ChurchTab? = null) {
        prefs.edit().putBoolean("key_onboarding_completed", true).apply()
        _uiState.update {
            it.copy(
                isOnboardingCompleted = true,
                isOnboardingReviewOpen = false,
                selectedTab = openTabAfter ?: it.selectedTab
            )
        }
    }

    fun openOnboardingReview() {
        _uiState.update { it.copy(isOnboardingReviewOpen = true) }
    }

    fun closeOnboardingReview() {
        _uiState.update { it.copy(isOnboardingReviewOpen = false) }
    }

    fun resetToOnboarding() {
        prefs.edit().putBoolean("key_onboarding_completed", false).apply()
        _uiState.update { it.copy(isOnboardingCompleted = false, isOnboardingReviewOpen = false) }
    }

    fun selectTab(tab: ChurchTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun setScriptureBook(book: BibleBook, chapterNum: Int = 1, verseNum: Int? = null) {
        val safeChapter = if (book.chapters.isNotEmpty()) {
            book.chapters.find { it.chapterNumber == chapterNum }?.chapterNumber
                ?: book.chapters.firstOrNull()?.chapterNumber ?: 1
        } else {
            chapterNum.coerceIn(1, book.chapterCount.coerceAtLeast(1))
        }
        _uiState.update {
            it.copy(
                selectedBook = book,
                selectedChapterNumber = safeChapter,
                selectedVerseNumber = verseNum
            )
        }
    }

    fun setScriptureChapter(chapterNum: Int, verseNum: Int? = null) {
        val maxChapters = _uiState.value.selectedBook.chapterCount.coerceAtLeast(1)
        val validChapter = chapterNum.coerceIn(1, maxChapters)
        _uiState.update {
            it.copy(
                selectedChapterNumber = validChapter,
                selectedVerseNumber = verseNum
            )
        }
    }

    fun setScriptureVerse(verseNum: Int?) {
        _uiState.update { it.copy(selectedVerseNumber = verseNum) }
    }

    fun setScripturePassage(bookName: String, chapterNum: Int, verseNum: Int? = null) {
        val foundBook = ChurchDataSeed.findBookByName(bookName)
        if (foundBook != null) {
            setScriptureBook(foundBook, chapterNum, verseNum)
        }
    }

    fun setTranslation(translation: String) {
        prefs.edit().putString("key_bible_translation", translation).apply()
        _uiState.update { it.copy(selectedTranslation = translation) }
    }

    fun setReaderFontSize(sizeSp: Float) {
        val safeSize = sizeSp.coerceIn(13f, 26f)
        prefs.edit().putFloat("key_reader_font_size", safeSize).apply()
        _uiState.update { it.copy(readerFontSizeSp = safeSize) }
    }

    fun setScriptureSearchQuery(query: String) {
        _uiState.update { it.copy(scriptureSearchQuery = query) }
    }

    fun toggleBookmark(book: String, chapter: Int, verse: Int, text: String) {
        viewModelScope.launch {
            repository.toggleBookmark(
                book = book,
                chapter = chapter,
                verse = verse,
                text = text,
                translation = _uiState.value.selectedTranslation
            )
            showToast("Bookmark updated for $book $chapter:$verse")
        }
    }

    // Audio Player Controls
    fun playSermon(sermon: Sermon) {
        _uiState.update {
            it.copy(
                activeSermon = sermon,
                isAudioPlaying = true,
                audioProgress = 0.05f
            )
        }
        startAudioTicker()
    }

    fun togglePlayPause() {
        val playing = !_uiState.value.isAudioPlaying
        _uiState.update { it.copy(isAudioPlaying = playing) }
        if (playing) {
            startAudioTicker()
        } else {
            audioPlaybackJob?.cancel()
        }
    }

    fun seekAudio(progress: Float) {
        _uiState.update { it.copy(audioProgress = progress.coerceIn(0f, 1f)) }
    }

    fun cycleAudioSpeed() {
        val nextSpeed = when (_uiState.value.audioSpeed) {
            1.0f -> 1.25f
            1.25f -> 1.5f
            1.5f -> 2.0f
            else -> 1.0f
        }
        _uiState.update { it.copy(audioSpeed = nextSpeed) }
    }

    private fun startAudioTicker() {
        audioPlaybackJob?.cancel()
        audioPlaybackJob = viewModelScope.launch {
            while (_uiState.value.isAudioPlaying) {
                delay(1000)
                val current = _uiState.value.audioProgress
                if (current >= 1.0f) {
                    _uiState.update { it.copy(isAudioPlaying = false, audioProgress = 0f) }
                    break
                } else {
                    _uiState.update { it.copy(audioProgress = (current + 0.005f * it.audioSpeed).coerceAtMost(1f)) }
                }
            }
        }
    }

    // Devotion
    fun selectDevotional(devotional: Devotional) {
        _uiState.update { it.copy(selectedDevotional = devotional) }
        observeDevotionJournal(devotional.id)
    }

    fun toggleFavoriteDevotion(devotionId: String) {
        viewModelScope.launch {
            val wasFav = _uiState.value.favoriteDevotionIds.contains(devotionId)
            repository.toggleFavoriteDevotion(devotionId)
            val dev = ChurchDataSeed.devotionals.find { it.id == devotionId }
            val title = dev?.title ?: "Devotion"
            if (wasFav) {
                showToast("Removed \"$title\" from Favorites")
            } else {
                showToast("Saved \"$title\" to Favorites")
            }
        }
    }

    fun setDevotionsFilterFavoritesOnly(favoritesOnly: Boolean) {
        _uiState.update { it.copy(devotionsFilterFavoritesOnly = favoritesOnly) }
    }

    fun saveJournalEntry(devotionId: String, reflection: String, prayer: String) {
        viewModelScope.launch {
            val dev = ChurchDataSeed.devotionals.find { it.id == devotionId } ?: _uiState.value.selectedDevotional
            repository.saveJournal(
                devotionId = devotionId,
                dateString = dev.date,
                title = dev.title,
                reflectionText = reflection,
                prayerText = prayer,
                category = "Devotion Reflection",
                mood = "Peaceful"
            )
            showToast("Reflection & Prayer saved to Journal")
        }
    }

    // Journal Section & Notes
    fun setJournalSearchQuery(query: String) {
        _uiState.update { it.copy(journalSearchQuery = query) }
    }

    fun setSelectedJournalCategory(category: String) {
        _uiState.update { it.copy(selectedJournalCategory = category) }
    }

    fun openNewJournalModal(entry: JournalEntryEntity? = null, initialDevotionId: String = "") {
        _uiState.update {
            it.copy(
                isShowingNewJournalModal = true,
                editingJournalEntry = entry
            )
        }
    }

    fun closeNewJournalModal() {
        _uiState.update {
            it.copy(
                isShowingNewJournalModal = false,
                editingJournalEntry = null
            )
        }
    }

    fun saveCustomJournal(
        title: String,
        reflectionText: String,
        prayerText: String,
        category: String,
        mood: String,
        id: Int = 0,
        devotionId: String = ""
    ) {
        viewModelScope.launch {
            val dateStr = if (id > 0) {
                _uiState.value.editingJournalEntry?.dateString ?: "Today, ${java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(java.util.Date())}"
            } else {
                "Today, ${java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(java.util.Date())}"
            }
            repository.saveJournal(
                devotionId = devotionId,
                dateString = dateStr,
                title = title.ifBlank { "Spiritual Reflection" },
                reflectionText = reflectionText,
                prayerText = prayerText,
                category = category,
                mood = mood,
                id = id
            )
            _uiState.update { it.copy(isShowingNewJournalModal = false, editingJournalEntry = null) }
            showToast(if (id > 0) "Journal entry updated" else "Journal entry saved to Sanctuary notes")
        }
    }

    fun deleteJournalEntry(id: Int) {
        viewModelScope.launch {
            repository.deleteJournal(id)
            showToast("Journal entry removed")
        }
    }

    // Community & Prayer
    fun setAreaFilter(area: String) {
        _uiState.update { it.copy(selectedAreaFilter = area) }
    }

    fun toggleJoinGroup(groupId: String) {
        viewModelScope.launch {
            val isJoined = joinedGroups.value.contains(groupId)
            repository.toggleJoinGroup(groupId, isJoined)
            val group = ChurchDataSeed.prayerGroups.find { it.id == groupId }
            val groupName = group?.name ?: "Group"
            if (!isJoined) {
                showToast("Joined $groupName! Meeting details saved.")
            } else {
                showToast("Left $groupName")
            }
        }
    }

    fun prayForRequest(id: Int) {
        viewModelScope.launch {
            repository.prayForRequest(id)
            showToast("Prayed! Thank you for standing in agreement.")
        }
    }

    fun submitPrayerRequest(author: String, isAnon: Boolean, area: String, title: String, details: String) {
        viewModelScope.launch {
            repository.addPrayerRequest(author, isAnon, area, title, details)
            _uiState.update { it.copy(isShowingPrayerModal = false) }
            showToast("Prayer request posted to community wall")
        }
    }

    fun openPrayerModal() {
        _uiState.update { it.copy(isShowingPrayerModal = true) }
    }

    fun closePrayerModal() {
        _uiState.update { it.copy(isShowingPrayerModal = false) }
    }

    fun setPastorSearchQuery(query: String) {
        _uiState.update { it.copy(pastorSearchQuery = query) }
    }

    fun setPastorSpecialtyFilter(specialty: String) {
        _uiState.update { it.copy(selectedPastorSpecialtyFilter = specialty) }
    }

    fun openPastorProfile(pastor: Pastor) {
        _uiState.update { it.copy(isShowingPastorProfileModal = true, selectedPastorForProfile = pastor) }
    }

    fun closePastorProfile() {
        _uiState.update { it.copy(isShowingPastorProfileModal = false, selectedPastorForProfile = null) }
    }

    fun openGuidanceComposer(pastor: Pastor? = null, starterPrompt: String? = null, category: String? = null) {
        _uiState.update {
            it.copy(
                isShowingGuidanceComposerModal = true,
                selectedPastorForContact = pastor ?: ChurchDataSeed.pastors.first(),
                prefillGuidancePrompt = starterPrompt ?: "",
                prefillGuidanceCategory = category ?: "Spiritual Guidance & Discernment"
            )
        }
    }

    fun closeGuidanceComposer() {
        _uiState.update {
            it.copy(
                isShowingGuidanceComposerModal = false,
                selectedPastorForContact = null,
                prefillGuidancePrompt = ""
            )
        }
    }

    fun openPastorContactModal(pastor: Pastor) {
        openGuidanceComposer(pastor)
    }

    fun closePastorContactModal() {
        closeGuidanceComposer()
    }

    fun sendGuidanceMessage(
        context: Context,
        pastor: Pastor,
        senderName: String,
        senderEmail: String,
        category: String,
        urgency: String,
        subject: String,
        content: String
    ) {
        viewModelScope.launch {
            val messageId = repository.sendPastorMessage(
                pastorId = pastor.id,
                pastorName = pastor.name,
                pastorTitle = pastor.title,
                senderName = senderName,
                senderEmail = senderEmail,
                messageType = category,
                urgency = urgency,
                subject = if (subject.isNotBlank()) subject else "Guidance on $category",
                content = content
            )
            _uiState.update {
                it.copy(
                    isShowingGuidanceComposerModal = false,
                    isShowingPastorContactModal = false,
                    selectedPastorForContact = null,
                    prefillGuidancePrompt = ""
                )
            }
            NotificationHelper.sendPastorResponseNotification(context, pastor.name, content.take(60))
            showToast("Guidance request submitted to ${pastor.name}")

            // Simulate authentic pastoral response & prayer commitment after a short moment
            launch {
                delay(2200)
                val (pastoralReply, scripture) = generatePastoralGuidance(pastor, category, content)
                repository.providePastorReply(
                    messageId = messageId.toInt(),
                    reply = pastoralReply,
                    scriptureGuidance = scripture,
                    status = "Guidance Provided"
                )
                NotificationHelper.sendPastorGuidanceRepliedNotification(
                    context = context,
                    pastorName = pastor.name,
                    preview = pastoralReply.take(65),
                    scripture = scripture
                )
            }
        }
    }

    fun sendPastorMessage(
        context: Context,
        pastor: Pastor,
        senderName: String,
        senderEmail: String,
        messageType: String,
        content: String
    ) {
        sendGuidanceMessage(
            context = context,
            pastor = pastor,
            senderName = senderName,
            senderEmail = senderEmail,
            category = messageType,
            urgency = "Standard",
            subject = "Pastoral Message",
            content = content
        )
    }

    fun replyToGuidanceThread(
        context: Context,
        thread: PastorMessageEntity,
        followUpContent: String
    ) {
        viewModelScope.launch {
            val updatedContent = "${thread.content}\n\n[Follow-up question by ${thread.senderName}]:\n$followUpContent"
            val pastor = ChurchDataSeed.pastors.find { it.id == thread.pastorId } ?: ChurchDataSeed.pastors.first()
            val existingReply = thread.pastorReply

            repository.providePastorReply(
                messageId = thread.id,
                reply = "$existingReply\n\n[${pastor.name} Follow-up Counsel]:\nThank you for following up. As you take this step, remember that the Lord is faithful to complete the good work He began in you. Keep fixing your eyes on Jesus!",
                scriptureGuidance = thread.scriptureGuidance,
                status = "In Active Dialogue"
            )
            showToast("Follow-up sent to ${thread.pastorName}")
        }
    }

    fun deleteGuidanceMessage(messageId: Int) {
        viewModelScope.launch {
            repository.deletePastorMessage(messageId)
            showToast("Message thread removed")
        }
    }

    private fun generatePastoralGuidance(pastor: Pastor, category: String, content: String): Pair<String, String> {
        return when (pastor.id) {
            "pastor_sarah" -> {
                Pair(
                    "Dear friend in Christ, thank you for reaching out with honesty. Walking through tender seasons or emotional stress requires us to release the burden of carrying tomorrow. I have brought your situation before the Lord in prayer today. Please know that God's grace is sufficient for right now. If you would like to set up a private counseling session in our Pastoral Care Center, let me know.",
                    "The Lord is close to the brokenhearted and saves those who are crushed in spirit. — Psalm 34:18"
                )
            }
            "pastor_david" -> {
                Pair(
                    "Grace and peace to you. In seeking biblical discernment, we are reminded that God's Word is a lamp unto our feet and a light unto our path. As you pray over this decision, continue to test all things against Scripture and seek godly wisdom from your church community. You are covered in our prayers.",
                    "If any of you lacks wisdom, you should ask God, who gives generously to all without finding fault, and it will be given to you. — James 1:5"
                )
            }
            "pastor_marcus" -> {
                Pair(
                    "Hey there! So grateful you reached out. Navigating life questions, faith tensions, or campus/work pressure is something no one should do alone. God has intentionally placed you where you are to shine His light. Let's connect this Sunday after service for a quick check-in!",
                    "Don't let anyone look down on you because you are young, but set an example for the believers in speech, in conduct, in love, in faith and in purity. — 1 Timothy 4:12"
                )
            }
            "pastor_elena" -> {
                Pair(
                    "Beloved, I am standing in intercession with you right now. In moments of uncertainty or spiritual longing, lifting our hearts in worship and quiet contemplative prayer brings profound peace. May the Holy Spirit fill your spirit with renewed joy and assurance.",
                    "One thing I ask from the Lord, this only do I seek: that I may dwell in the house of the Lord all the days of my life, to gaze on the beauty of the Lord. — Psalm 27:4"
                )
            }
            else -> {
                Pair(
                    "Greetings and blessings! Thank you for sharing your heart. God is actively at work in your life, refining your faith and opening doors for His kingdom. Our whole pastoral team is standing with you in prayer.",
                    "He has shown you, O mortal, what is good. And what does the Lord require of you? To act justly and to love mercy and to walk humbly with your God. — Micah 6:8"
                )
            }
        }
    }

    // Theme & Appearance Customization
    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString("key_theme_mode", mode.name).apply()
        _uiState.update { it.copy(themeMode = mode) }
        showToast("Theme updated to ${mode.title}")
    }

    fun setAccentTheme(accent: AccentTheme) {
        prefs.edit().putString("key_accent_theme", accent.name).apply()
        _uiState.update { it.copy(accentTheme = accent) }
        showToast("Accent palette changed to ${accent.title}")
    }

    fun setFontPreset(preset: FontPreset) {
        prefs.edit().putString("key_font_preset", preset.name).apply()
        _uiState.update { it.copy(fontPreset = preset) }
        showToast("Typography preset updated: ${preset.title}")
    }

    // Push Notifications
    fun toggleDailyVerseNotifications(enabled: Boolean) {
        prefs.edit().putBoolean("key_daily_verse_notif", enabled).apply()
        _uiState.update { it.copy(dailyVerseNotificationEnabled = enabled) }
    }

    fun toggleMeetingReminders(enabled: Boolean) {
        prefs.edit().putBoolean("key_meeting_reminder_notif", enabled).apply()
        _uiState.update { it.copy(meetingReminderEnabled = enabled) }
    }

    fun setDailyVerseTime(time: String) {
        prefs.edit().putString("key_daily_verse_time", time).apply()
        _uiState.update { it.copy(dailyVerseTime = time) }
        showToast("Daily verse scheduled for $time")
    }

    fun triggerTestDailyVersePush(context: Context) {
        NotificationHelper.sendDailyVerseNotification(context, ChurchDataSeed.dailyVerse)
        showToast("Daily Verse push notification sent!")
    }

    fun triggerTestMeetingPush(context: Context, group: PrayerGroup) {
        NotificationHelper.sendMeetingReminderNotification(context, group)
        showToast("Meeting reminder notification sent for ${group.name}!")
    }

    fun openNotificationSettings() {
        _uiState.update { it.copy(isNotificationSettingsOpen = true) }
    }

    fun closeNotificationSettings() {
        _uiState.update { it.copy(isNotificationSettingsOpen = false) }
    }

    fun showToast(message: String) {
        _uiState.update { it.copy(userToastMessage = message) }
        viewModelScope.launch {
            delay(3200)
            _uiState.update { if (it.userToastMessage == message) it.copy(userToastMessage = null) else it }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(userToastMessage = null) }
    }
}
