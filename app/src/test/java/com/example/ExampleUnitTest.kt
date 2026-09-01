package com.example

import com.example.ui.ChurchTab
import com.example.ui.navigation.ChurchScreen
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testNavigationTabs_and_Routes() {
    // Verify core views exist and map correctly to routes
    assertEquals(ChurchScreen.Scripture, ChurchScreen.fromTab(ChurchTab.SCRIPTURE))
    assertEquals(ChurchScreen.Devotion, ChurchScreen.fromTab(ChurchTab.DEVOTION))
    assertEquals(ChurchScreen.Profile, ChurchScreen.fromTab(ChurchTab.PROFILE))
    assertEquals(ChurchScreen.Home, ChurchScreen.fromTab(ChurchTab.HOME))

    // Verify reverse lookup
    assertEquals(ChurchScreen.Scripture, ChurchScreen.fromRoute("screen_scripture"))
    assertEquals(ChurchScreen.Devotion, ChurchScreen.fromRoute("screen_devotion"))
    assertEquals(ChurchScreen.Profile, ChurchScreen.fromRoute("screen_profile"))
  }

  @Test
  fun testDailyVerseModelAndSeed() {
    val dailyVerse = com.example.data.repository.ChurchDataSeed.dailyVerse
    assertNotNull(dailyVerse)
    assertTrue(dailyVerse.reference.isNotBlank())
    assertTrue(dailyVerse.text.isNotBlank())
    assertTrue(dailyVerse.date.isNotBlank())
  }
}

