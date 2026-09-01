package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long

    @Query("DELETE FROM bookmarks WHERE book = :book AND chapter = :chapter AND verse = :verse")
    suspend fun deleteBookmark(book: String, chapter: Int, verse: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE book = :book AND chapter = :chapter AND verse = :verse)")
    fun isBookmarked(book: String, chapter: Int, verse: Int): Flow<Boolean>
}

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun getAllJournals(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE devotionId = :devotionId LIMIT 1")
    fun getJournalForDevotion(devotionId: String): Flow<JournalEntryEntity?>

    @Query("SELECT * FROM journal_entries WHERE title LIKE '%' || :query || '%' OR reflectionText LIKE '%' || :query || '%' OR prayerText LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchJournals(query: String): Flow<List<JournalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveJournal(entry: JournalEntryEntity): Long

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteJournal(id: Int)
}

@Dao
interface FavoriteDevotionDao {
    @Query("SELECT devotionId FROM favorite_devotions")
    fun getAllFavoriteIds(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_devotions WHERE devotionId = :devotionId)")
    fun isFavorite(devotionId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteDevotionEntity)

    @Query("DELETE FROM favorite_devotions WHERE devotionId = :devotionId")
    suspend fun removeFavorite(devotionId: String)
}

@Dao
interface PrayerDao {
    @Query("SELECT * FROM prayer_requests ORDER BY timestamp DESC")
    fun getAllPrayerRequests(): Flow<List<PrayerRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayerRequest(request: PrayerRequestEntity): Long

    @Query("UPDATE prayer_requests SET prayerCount = prayerCount + 1 WHERE id = :id")
    suspend fun incrementPrayerCount(id: Int)
}

@Dao
interface PastorMessageDao {
    @Query("SELECT * FROM pastor_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<PastorMessageEntity>>

    @Query("SELECT * FROM pastor_messages WHERE pastorId = :pastorId ORDER BY timestamp DESC")
    fun getMessagesForPastor(pastorId: String): Flow<List<PastorMessageEntity>>

    @Query("SELECT * FROM pastor_messages WHERE id = :id LIMIT 1")
    fun getMessageById(id: Int): Flow<PastorMessageEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: PastorMessageEntity): Long

    @Query("UPDATE pastor_messages SET pastorReply = :reply, responseStatus = :status, scriptureGuidance = :scripture WHERE id = :id")
    suspend fun updateReply(id: Int, reply: String, status: String, scripture: String)

    @Query("DELETE FROM pastor_messages WHERE id = :id")
    suspend fun deleteMessage(id: Int)
}

@Dao
interface JoinedGroupDao {
    @Query("SELECT groupId FROM joined_groups")
    fun getJoinedGroupIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun joinGroup(group: JoinedGroupEntity)

    @Query("DELETE FROM joined_groups WHERE groupId = :groupId")
    suspend fun leaveGroup(groupId: String)
}

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements ORDER BY isPinned DESC, timestamp DESC")
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements WHERE status = 'Published' OR (isScheduled = 1 AND scheduledTimestamp <= :currentTime) ORDER BY isPinned DESC, timestamp DESC")
    fun getPublishedAnnouncements(currentTime: Long): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements WHERE isScheduled = 1 AND status = 'Scheduled' ORDER BY scheduledTimestamp ASC")
    fun getScheduledAnnouncements(): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements WHERE id = :id LIMIT 1")
    fun getAnnouncementById(id: Int): Flow<AnnouncementEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity): Long

    @Update
    suspend fun updateAnnouncement(announcement: AnnouncementEntity)

    @Query("DELETE FROM announcements WHERE id = :id")
    suspend fun deleteAnnouncement(id: Int)

    @Query("UPDATE announcements SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)

    @Query("UPDATE announcements SET notificationSent = 1 WHERE id = :id")
    suspend fun markNotificationSent(id: Int)
}
