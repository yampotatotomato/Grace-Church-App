package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.ChurchDataSeed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Church App", appName)
  }

  @Test
  fun `verify staff users are configured for Companion Portal`() {
    val staff = ChurchDataSeed.staffUsers
    assertTrue("Staff list should not be empty", staff.isNotEmpty())
    val seniorPastor = staff.find { it.role.contains("Senior Pastor", ignoreCase = true) }
    assertNotNull("Senior Pastor must exist", seniorPastor)
  }

  @Test
  fun `verify announcements data seed has default bulletins and pastoral letters`() {
    val announcements = ChurchDataSeed.initialAnnouncements
    assertTrue("Seed announcements should not be empty", announcements.isNotEmpty())
    val hasPinned = announcements.any { it.isPinned }
    assertTrue("Should contain at least one pinned announcement", hasPinned)
  }

  @Test
  fun `verify scripture canon and translations exist`() {
    val books = ChurchDataSeed.bibleBooks
    assertTrue("Bible books should not be empty", books.isNotEmpty())
    val romans = books.find { it.name == "Romans" }
    assertNotNull("Romans book must exist", romans)
    assertTrue("Romans chapter 8 must exist", romans!!.chapters.any { it.chapterNumber == 8 })
  }

  @Test
  fun `verify devotionals and prayer groups data seed`() {
    val devotions = ChurchDataSeed.devotionals
    assertTrue("Devotionals should not be empty", devotions.isNotEmpty())
    val prayerGroups = ChurchDataSeed.prayerGroups
    assertTrue("Prayer groups should not be empty", prayerGroups.isNotEmpty())
  }

  @Test
  fun `verify daily verses list has diverse themes and translations`() {
    val verses = ChurchDataSeed.dailyVersesList
    assertTrue("Daily verses list should have multiple verses", verses.size >= 5)
    
    val todayVerse = verses.first()
    assertNotNull("Today's verse reference should be present", todayVerse.reference)
    assertTrue("Verse text must not be blank", todayVerse.text.isNotBlank())
    assertTrue("NIV translation must be present", todayVerse.translationTexts.containsKey("NIV"))
    assertTrue("ESV translation must be present", todayVerse.translationTexts.containsKey("ESV"))
    assertTrue("KJV translation must be present", todayVerse.translationTexts.containsKey("KJV"))
    assertTrue("NLT translation must be present", todayVerse.translationTexts.containsKey("NLT"))
    
    val hasLoveTheme = verses.any { it.theme.contains("Love", ignoreCase = true) }
    assertTrue("Should include verses on God's Love", hasLoveTheme)
    
    val hasPeaceTheme = verses.any { it.theme.contains("Peace", ignoreCase = true) }
    assertTrue("Should include verses on Peace & Anxiety", hasPeaceTheme)
  }

  @Test
  fun `verify scripture reading goal and checkmark state tracking`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.ui.ChurchViewModel(app)

    // Verify initial goal target is at least 1
    val initialGoal = viewModel.uiState.value.dailyReadingGoalTarget
    assertTrue("Default reading goal should be >= 1", initialGoal >= 1)

    // Toggle verse completed
    val testVerse = "Romans 8:28"
    viewModel.toggleVerseReadingCompleted(testVerse)
    assertTrue(
      "Verse should be recorded in completedVerseKeysToday",
      viewModel.uiState.value.completedVerseKeysToday.contains(testVerse)
    )

    // Set goal to 1 and verify completion status
    viewModel.setDailyReadingGoalTarget(1)
    assertEquals(1, viewModel.uiState.value.dailyReadingGoalTarget)
    assertTrue(viewModel.uiState.value.completedVerseKeysToday.size >= 1)

    // Toggle chapter completed
    val chapterKey = "Romans 8"
    viewModel.toggleChapterReadingCompleted(chapterKey)
    assertTrue(
      "Chapter should be recorded in completedChapters",
      viewModel.uiState.value.completedChapters.contains(chapterKey)
    )
  }

  @Test
  fun `verify prayer groups filtering by category and search keyword`() {
    val allGroups = ChurchDataSeed.prayerGroups
    assertTrue("Should have seed prayer groups", allGroups.isNotEmpty())

    // Filter by Young Adults
    val youngAdultGroups = allGroups.filter { it.category.contains("Young Adults", ignoreCase = true) }
    assertTrue("Young adult groups should exist", youngAdultGroups.isNotEmpty())

    // Filter by Area / District
    val downtownGroups = allGroups.filter { it.area.contains("Downtown", ignoreCase = true) }
    assertTrue("Downtown groups should exist", downtownGroups.isNotEmpty())

    // Search query matching
    val query = "College"
    val searchResults = allGroups.filter {
      it.name.contains(query, ignoreCase = true) ||
      it.description.contains(query, ignoreCase = true) ||
      it.category.contains(query, ignoreCase = true)
    }
    assertTrue("Search for 'College' should return matching groups", searchResults.isNotEmpty())
  }
}
