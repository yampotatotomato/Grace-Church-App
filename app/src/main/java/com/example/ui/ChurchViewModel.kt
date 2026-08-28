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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ChurchTab(val title: String, val testTag: String) {
    HOME("Today", "tab_home"),
    SCRIPTURE("Scripture", "tab_scripture"),
    SERMONS("Sermons", "tab_sermons"),
    DEVOTION("Devotion", "tab_devotion"),
    COMMUNITY("Prayer & Groups", "tab_community")
}

data class ChurchUiState(
    val isOnboardingCompleted: Boolean = false,
    val selectedTab: ChurchTab = ChurchTab.HOME,
    // Scripture state
    val selectedBook: BibleBook = ChurchDataSeed.bibleBooks.first(),
    val selectedChapterNumber: Int = 5,
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
    // Community / Prayer State
    val selectedAreaFilter: String = "All Areas",
    val isShowingPrayerModal: Boolean = false,
    val isShowingPastorContactModal: Boolean = false,
    val selectedPastorForContact: Pastor? = null,
    // Settings & Notifications
    val dailyVerseNotificationEnabled: Boolean = true,
    val dailyVerseTime: String = "07:00 AM",
    val meetingReminderEnabled: Boolean = true,
    val isNotificationSettingsOpen: Boolean = false,
    val userToastMessage: String? = null
)

class ChurchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ChurchRepository.getInstance(application)

    private val _uiState = MutableStateFlow(ChurchUiState())
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

    private var audioPlaybackJob: Job? = null

    init {
        NotificationHelper.createNotificationChannels(application)
        observeDevotionJournal(_uiState.value.selectedDevotional.id)
    }

    private fun observeDevotionJournal(devotionId: String) {
        viewModelScope.launch {
            repository.getJournalForDevotion(devotionId).collect { entry ->
                _uiState.update { it.copy(currentDevotionJournal = entry) }
            }
        }
    }

    fun completeOnboarding() {
        _uiState.update { it.copy(isOnboardingCompleted = true) }
    }

    fun resetToOnboarding() {
        _uiState.update { it.copy(isOnboardingCompleted = false) }
    }

    fun selectTab(tab: ChurchTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun setScriptureBook(book: BibleBook, chapterNum: Int = 1) {
        val validChapter = if (book.chapters.isNotEmpty()) {
            book.chapters.firstOrNull()?.chapterNumber ?: 1
        } else {
            1
        }
        _uiState.update {
            it.copy(
                selectedBook = book,
                selectedChapterNumber = validChapter
            )
        }
    }

    fun setScriptureChapter(chapterNum: Int) {
        _uiState.update { it.copy(selectedChapterNumber = chapterNum) }
    }

    fun setTranslation(translation: String) {
        _uiState.update { it.copy(selectedTranslation = translation) }
    }

    fun setReaderFontSize(sizeSp: Float) {
        _uiState.update { it.copy(readerFontSizeSp = sizeSp.coerceIn(13f, 26f)) }
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

    fun saveJournalEntry(devotionId: String, reflection: String, prayer: String) {
        viewModelScope.launch {
            val dev = ChurchDataSeed.devotionals.find { it.id == devotionId } ?: _uiState.value.selectedDevotional
            repository.saveJournal(
                devotionId = devotionId,
                dateString = dev.date,
                title = dev.title,
                reflectionText = reflection,
                prayerText = prayer
            )
            showToast("Reflection & Prayer saved to Journal")
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

    fun openPastorContactModal(pastor: Pastor) {
        _uiState.update { it.copy(isShowingPastorContactModal = true, selectedPastorForContact = pastor) }
    }

    fun closePastorContactModal() {
        _uiState.update { it.copy(isShowingPastorContactModal = false, selectedPastorForContact = null) }
    }

    fun sendPastorMessage(
        context: Context,
        pastor: Pastor,
        senderName: String,
        senderEmail: String,
        messageType: String,
        content: String
    ) {
        viewModelScope.launch {
            repository.sendPastorMessage(
                pastorId = pastor.id,
                pastorName = pastor.name,
                senderName = senderName,
                senderEmail = senderEmail,
                messageType = messageType,
                content = content
            )
            _uiState.update { it.copy(isShowingPastorContactModal = false, selectedPastorForContact = null) }
            NotificationHelper.sendPastorResponseNotification(context, pastor.name, content.take(60))
            showToast("Message sent directly to ${pastor.name}")
        }
    }

    // Push Notifications
    fun toggleDailyVerseNotifications(enabled: Boolean) {
        _uiState.update { it.copy(dailyVerseNotificationEnabled = enabled) }
    }

    fun toggleMeetingReminders(enabled: Boolean) {
        _uiState.update { it.copy(meetingReminderEnabled = enabled) }
    }

    fun setDailyVerseTime(time: String) {
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
