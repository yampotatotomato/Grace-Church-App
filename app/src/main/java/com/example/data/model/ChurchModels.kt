package com.example.data.model

data class BibleBook(
    val id: String,
    val name: String,
    val testament: String, // "Old Testament" or "New Testament"
    val category: String, // "Gospels", "Epistles", "Wisdom", "Law", "History", "Prophecy"
    val chapterCount: Int,
    val chapters: List<BibleChapter> = emptyList()
)

data class BibleChapter(
    val bookName: String,
    val chapterNumber: Int,
    val verses: List<BibleVerse>
)

data class BibleVerse(
    val bookName: String,
    val chapter: Int,
    val verseNumber: Int,
    val text: String,
    val translation: String = "NIV"
)

data class DailyVerse(
    val reference: String,
    val text: String,
    val theme: String,
    val date: String,
    val book: String = "Romans",
    val chapter: Int = 8,
    val verse: Int = 38,
    val endVerse: Int = 39,
    val reflection: String = "Take a quiet moment to reflect on God's unwavering promises today. Meditate on how His grace sustains your path through every season.",
    val prayer: String = "Lord, anchor my heart in Your unfailing love. Guide my thoughts, words, and actions today. Amen.",
    val translationTexts: Map<String, String> = emptyMap()
)

data class Sermon(
    val id: String,
    val title: String,
    val pastorName: String,
    val pastorTitle: String,
    val pastorAvatar: String = "",
    val seriesName: String,
    val date: String,
    val durationMinutes: Int,
    val scriptureReference: String,
    val summary: String,
    val keyPoints: List<String>,
    val studyNotes: String,
    val audioUrl: String = "sermon_audio_sample.mp3"
)

data class Pastor(
    val id: String,
    val name: String,
    val title: String,
    val bio: String,
    val education: String = "",
    val yearsOfMinistry: String = "",
    val officeLocation: String = "",
    val email: String,
    val phone: String,
    val officeHours: String,
    val availabilityStatus: String = "Available for Guidance",
    val photoRes: Int = 0,
    val specialty: List<String> = listOf("Marriage & Family", "Spiritual Growth", "Biblical Counseling"),
    val favoriteScripture: String = "Proverbs 3:5-6",
    val guidancePromptStarters: List<String> = listOf("Seeking wisdom on God's calling", "Praying through life transition", "Marriage & family guidance")
)

data class Devotional(
    val id: String,
    val date: String,
    val title: String,
    val authorPastor: String,
    val scriptureRef: String,
    val scriptureText: String,
    val reflectionText: String,
    val guidedPrayer: String,
    val discussionQuestion: String,
    val readingTimeMinutes: Int = 4
)

data class PrayerGroup(
    val id: String,
    val name: String,
    val area: String, // "North District", "Downtown / Central", "Westside", "East Valley", "South Hills", "All Areas"
    val meetingDayTime: String,
    val locationName: String,
    val address: String,
    val leaderName: String,
    val leaderContact: String,
    val groupType: String, // "Young Adults", "Families & Couples", "Men's Fellowship", "Women's Grace", "Intercessory Prayer", "Bible Study", "All Welcome"
    val description: String,
    val memberCount: Int = 12,
    val category: String = groupType,
    val meetingFormat: String = "In-Person", // "In-Person", "Home Gathering", "Hybrid / Virtual"
    val dayOfWeek: String = "Wednesday", // "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    val distanceMiles: Double = 2.4,
    val tags: List<String> = listOf("Fellowship", "Prayer", "Scripture Study"),
    val latitude: Double = 34.0522,
    val longitude: Double = -118.2437
)

data class CompanionStaffUser(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val title: String,
    val avatarInitials: String,
    val accessLevel: String = "Full Publishing & Alerts",
    val defaultCategory: String = "Pastoral Letter"
)

enum class AnnouncementCategory(val displayName: String, val tag: String) {
    PASTORAL_LETTER("Pastoral Letter", "pastoral_letter"),
    URGENT_ANNOUNCEMENT("Urgent Announcement", "urgent_announcement"),
    SERMON_STUDY_NOTES("Sermon Study Notes", "sermon_notes"),
    PRAYER_BULLETIN("Prayer Bulletin", "prayer_bulletin"),
    EVENT_GATHERING("Event & Gathering", "event_gathering"),
    MINISTRY_UPDATE("Ministry Update", "ministry_update")
}
