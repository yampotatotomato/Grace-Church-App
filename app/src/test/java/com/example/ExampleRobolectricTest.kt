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
}
