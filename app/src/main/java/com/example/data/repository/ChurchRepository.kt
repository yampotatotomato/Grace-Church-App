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

    init {
        // Seed initial prayer requests & a sample journal reflection if database is empty
        scope.launch {
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

    // Static / Catalog Data
    fun getDailyVerse(): DailyVerse = ChurchDataSeed.dailyVerse
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

    suspend fun sendPastorMessage(
        pastorId: String,
        pastorName: String,
        senderName: String,
        senderEmail: String,
        messageType: String,
        content: String
    ) {
        pastorMessageDao.insertMessage(
            PastorMessageEntity(
                pastorId = pastorId,
                pastorName = pastorName,
                senderName = senderName,
                senderEmail = senderEmail,
                messageType = messageType,
                content = content,
                responseStatus = "Sent to Pastor"
            )
        )
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
