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
    val date: String
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
    val area: String, // "Northside", "Downtown / Central", "Westside", "East Valley", "South Suburbs"
    val meetingDayTime: String,
    val locationName: String,
    val address: String,
    val leaderName: String,
    val leaderContact: String,
    val groupType: String, // "Young Adults", "Families", "Men's Fellowship", "Women's Grace", "All Welcome"
    val description: String,
    val memberCount: Int = 12
)
