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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveJournal(entry: JournalEntryEntity): Long
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: PastorMessageEntity): Long
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
