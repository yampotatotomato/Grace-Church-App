package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val book: String,
    val chapter: Int,
    val verse: Int,
    val text: String,
    val translation: String,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val devotionId: String = "",
    val dateString: String,
    val title: String,
    val reflectionText: String,
    val prayerText: String = "",
    val category: String = "Reflection", // "Reflection", "Gratitude", "Prayer", "Scripture Study"
    val mood: String = "Peaceful", // "Peaceful", "Grateful", "Seeking Guidance", "Joyful", "Humbled"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_devotions")
data class FavoriteDevotionEntity(
    @PrimaryKey val devotionId: String,
    val favoritedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "prayer_requests")
data class PrayerRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val authorName: String,
    val isAnonymous: Boolean = false,
    val area: String,
    val title: String,
    val details: String,
    val prayerCount: Int = 1,
    val isAnswered: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "pastor_messages")
data class PastorMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pastorId: String,
    val pastorName: String,
    val pastorTitle: String = "Pastor",
    val senderName: String,
    val senderEmail: String,
    val messageType: String, // "Spiritual Guidance", "Biblical Counseling", "Prayer Request", "Scripture Question", "General"
    val urgency: String = "Standard", // "Standard", "Urgent Care", "Confidential Meeting"
    val subject: String = "Pastoral Guidance Request",
    val content: String,
    val pastorReply: String = "",
    val scriptureGuidance: String = "",
    val responseStatus: String = "Received", // "Received", "Guidance Provided", "In Prayer", "Meeting Scheduled"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "joined_groups")
data class JoinedGroupEntity(
    @PrimaryKey val groupId: String,
    val joinedTimestamp: Long = System.currentTimeMillis()
)
