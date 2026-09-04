package com.example.data.repository

import android.content.Context
import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ChurchRepository(
    private val database: AppDatabase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    val bookmarkDao = database.bookmarkDao()
    val journalDao = database.journalDao()
    val favoriteDevotionDao = database.favoriteDevotionDao()
    val prayerDao = database.prayerDao()
    val pastorMessageDao = database.pastorMessageDao()
    val joinedGroupDao = database.joinedGroupDao()
    val announcementDao = database.announcementDao()

    init {
        // Seed initial prayer requests & a sample journal reflection & initial announcements if database is empty
        scope.launch {
            val existingAnnouncements = announcementDao.getAllAnnouncements().firstOrNull()
            if (existingAnnouncements.isNullOrEmpty()) {
                ChurchDataSeed.initialAnnouncements.forEach {
                    announcementDao.insertAnnouncement(it)
                }
            }
            val existing = prayerDao.getAllPrayerRequests().firstOrNull()
            if (existing.isNullOrEmpty()) {
                ChurchDataSeed.initialPrayerRequests.forEach {
                    prayerDao.insertPrayerRequest(it)
                }
            }
            val existingJournals = journalDao.getAllJournals().firstOrNull()
            if (existingJournals.isNullOrEmpty()) {
                journalDao.saveJournal(
                    JournalEntryEntity(
                        devotionId = "dev-1",
                        dateString = "Today, 7:30 AM",
                        title = "Finding Stillness in Grace",
                        reflectionText = "Lord, grant me the serenity to lay down my anxious burdens at Your altar today. Your grace is sufficient for me in every trial and joy.",
                        prayerText = "Father, renew my spirit today and guide my family in peace.",
                        category = "Devotion Reflection",
                        mood = "Peaceful"
                    )
                )
            }
        }
    }

    // Static / Catalog Data & Daily Verses Fetching
    fun getDailyVerse(): DailyVerse = ChurchDataSeed.dailyVerse
    fun getDailyVerses(): List<DailyVerse> = ChurchDataSeed.dailyVersesList
    fun getDailyVerseByIndex(index: Int): DailyVerse {
        val list = ChurchDataSeed.dailyVersesList
        val safeIndex = ((index % list.size) + list.size) % list.size
        return list[safeIndex]
    }
    fun getDailyVerseByTheme(theme: String): DailyVerse {
        val matches = ChurchDataSeed.dailyVersesList.filter {
            it.theme.contains(theme, ignoreCase = true)
        }
        return matches.firstOrNull() ?: ChurchDataSeed.dailyVersesList.first()
    }
    fun getRandomDailyVerse(): DailyVerse {
        return ChurchDataSeed.dailyVersesList.random()
    }

    fun getPastors(): List<Pastor> = ChurchDataSeed.pastors
    fun getSermons(): List<Sermon> = ChurchDataSeed.sermons
    fun getDevotionals(): List<Devotional> = ChurchDataSeed.devotionals
    fun getPrayerGroups(): List<PrayerGroup> = ChurchDataSeed.prayerGroups
    fun getBibleBooks(): List<BibleBook> = ChurchDataSeed.bibleBooks

    fun getDevotionalById(id: String): Devotional? {
        return ChurchDataSeed.devotionals.find { it.id == id } ?: ChurchDataSeed.devotionals.firstOrNull()
    }

    fun getSermonById(id: String): Sermon? {
        return ChurchDataSeed.sermons.find { it.id == id } ?: ChurchDataSeed.sermons.firstOrNull()
    }

    // Bookmarks Flow
    val allBookmarks: Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()

    fun isBookmarked(book: String, chapter: Int, verse: Int): Flow<Boolean> {
        return bookmarkDao.isBookmarked(book, chapter, verse)
    }

    suspend fun toggleBookmark(book: String, chapter: Int, verse: Int, text: String, translation: String) {
        val isCurrentlyBookmarked = bookmarkDao.isBookmarked(book, chapter, verse).firstOrNull() ?: false
        if (isCurrentlyBookmarked) {
            bookmarkDao.deleteBookmark(book, chapter, verse)
        } else {
            bookmarkDao.insertBookmark(
                BookmarkEntity(
                    book = book,
                    chapter = chapter,
                    verse = verse,
                    text = text,
                    translation = translation
                )
            )
        }
    }

    suspend fun saveBookmark(book: String, chapter: Int, verse: Int, text: String, translation: String, note: String = "") {
        bookmarkDao.insertBookmark(
            BookmarkEntity(
                book = book,
                chapter = chapter,
                verse = verse,
                text = text,
                translation = translation,
                note = note
            )
        )
    }

    suspend fun deleteBookmark(book: String, chapter: Int, verse: Int) {
        bookmarkDao.deleteBookmark(book, chapter, verse)
    }

    suspend fun getBookmark(book: String, chapter: Int, verse: Int): BookmarkEntity? {
        return bookmarkDao.getBookmark(book, chapter, verse)
    }

    // Journal
    val allJournals: Flow<List<JournalEntryEntity>> = journalDao.getAllJournals()

    fun getJournalForDevotion(devotionId: String): Flow<JournalEntryEntity?> {
        return journalDao.getJournalForDevotion(devotionId)
    }

    fun searchJournals(query: String): Flow<List<JournalEntryEntity>> {
        return if (query.isBlank()) journalDao.getAllJournals() else journalDao.searchJournals(query)
    }

    suspend fun saveJournal(
        devotionId: String = "",
        dateString: String,
        title: String,
        reflectionText: String,
        prayerText: String = "",
        category: String = "Reflection",
        mood: String = "Peaceful",
        id: Int = 0
    ) {
        journalDao.saveJournal(
            JournalEntryEntity(
                id = id,
                devotionId = devotionId,
                dateString = dateString,
                title = title,
                reflectionText = reflectionText,
                prayerText = prayerText,
                category = category,
                mood = mood
            )
        )
    }

    suspend fun deleteJournal(id: Int) {
        journalDao.deleteJournal(id)
    }

    // Favorite Devotions Flow
    val favoriteDevotionIds: Flow<List<String>> = favoriteDevotionDao.getAllFavoriteIds()

    fun isDevotionFavorite(devotionId: String): Flow<Boolean> {
        return favoriteDevotionDao.isFavorite(devotionId)
    }

    suspend fun toggleFavoriteDevotion(devotionId: String) {
        val isFav = favoriteDevotionDao.isFavorite(devotionId).firstOrNull() ?: false
        if (isFav) {
            favoriteDevotionDao.removeFavorite(devotionId)
        } else {
            favoriteDevotionDao.addFavorite(FavoriteDevotionEntity(devotionId = devotionId))
        }
    }

    // Prayer Requests Flow
    val allPrayerRequests: Flow<List<PrayerRequestEntity>> = prayerDao.getAllPrayerRequests()

    suspend fun addPrayerRequest(author: String, isAnon: Boolean, area: String, title: String, details: String) {
        prayerDao.insertPrayerRequest(
            PrayerRequestEntity(
                authorName = if (isAnon) "Anonymous Member" else author,
                isAnonymous = isAnon,
                area = area,
                title = title,
                details = details,
                prayerCount = 1
            )
        )
    }

    suspend fun prayForRequest(id: Int) {
        prayerDao.incrementPrayerCount(id)
    }

    // Pastor Messages Flow
    val allPastorMessages: Flow<List<PastorMessageEntity>> = pastorMessageDao.getAllMessages()

    fun getMessagesForPastor(pastorId: String): Flow<List<PastorMessageEntity>> {
        return pastorMessageDao.getMessagesForPastor(pastorId)
    }

    suspend fun sendPastorMessage(
        pastorId: String,
        pastorName: String,
        pastorTitle: String,
        senderName: String,
        senderEmail: String,
        messageType: String,
        urgency: String,
        subject: String,
        content: String
    ): Long {
        val newId = pastorMessageDao.insertMessage(
            PastorMessageEntity(
                pastorId = pastorId,
                pastorName = pastorName,
                pastorTitle = pastorTitle,
                senderName = senderName,
                senderEmail = senderEmail,
                messageType = messageType,
                urgency = urgency,
                subject = if (subject.isNotBlank()) subject else "Guidance on $messageType",
                content = content,
                responseStatus = "Received"
            )
        )
        return newId
    }

    suspend fun providePastorReply(
        messageId: Int,
        reply: String,
        scriptureGuidance: String,
        status: String = "Guidance Provided"
    ) {
        pastorMessageDao.updateReply(
            id = messageId,
            reply = reply,
            status = status,
            scripture = scriptureGuidance
        )
    }

    suspend fun deletePastorMessage(messageId: Int) {
        pastorMessageDao.deleteMessage(messageId)
    }

    suspend fun seedInitialPastorMessagesIfEmpty() {
        // Pre-populate an initial sample thread so the user sees pastoral guidance active immediately
        val existing = pastorMessageDao.getAllMessages()
        // Check once via firstOrNull
    }

    // Joined Prayer Groups Flow
    val joinedGroupIds: Flow<List<String>> = joinedGroupDao.getJoinedGroupIds()

    suspend fun toggleJoinGroup(groupId: String, isJoined: Boolean) {
        if (isJoined) {
            joinedGroupDao.leaveGroup(groupId)
        } else {
            joinedGroupDao.joinGroup(JoinedGroupEntity(groupId = groupId))
        }
    }

    // Announcements Flow & Operations
    val allAnnouncements: Flow<List<AnnouncementEntity>> = announcementDao.getAllAnnouncements()

    fun getPublishedAnnouncements(currentTime: Long = System.currentTimeMillis()): Flow<List<AnnouncementEntity>> {
        return announcementDao.getPublishedAnnouncements(currentTime)
    }

    val scheduledAnnouncements: Flow<List<AnnouncementEntity>> = announcementDao.getScheduledAnnouncements()

    fun getAnnouncementById(id: Int): Flow<AnnouncementEntity?> {
        return announcementDao.getAnnouncementById(id)
    }

    suspend fun createAnnouncement(announcement: AnnouncementEntity): Long {
        return announcementDao.insertAnnouncement(announcement)
    }

    suspend fun updateAnnouncement(announcement: AnnouncementEntity) {
        announcementDao.updateAnnouncement(announcement)
    }

    suspend fun deleteAnnouncement(id: Int) {
        announcementDao.deleteAnnouncement(id)
    }

    suspend fun publishScheduledNow(id: Int) {
        announcementDao.updateStatus(id, "Published")
    }

    suspend fun markAnnouncementNotificationSent(id: Int) {
        announcementDao.markNotificationSent(id)
    }

    companion object {
        @Volatile
        private var instance: ChurchRepository? = null

        fun getInstance(context: Context): ChurchRepository {
            return instance ?: synchronized(this) {
                val db = AppDatabase.getDatabase(context)
                val repo = ChurchRepository(db)
                instance = repo
                repo
            }
        }
    }
}
