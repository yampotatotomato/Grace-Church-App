package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.repository.ChurchDataSeed

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("EXTRA_TYPE") ?: "VERSE"
        when (type) {
            "VERSE" -> {
                NotificationHelper.sendDailyVerseNotification(context, ChurchDataSeed.dailyVerse)
            }
            "MEETING" -> {
                val group = ChurchDataSeed.prayerGroups.firstOrNull() ?: return
                NotificationHelper.sendMeetingReminderNotification(context, group)
            }
            "ANNOUNCEMENT" -> {
                val title = intent.getStringExtra("EXTRA_TITLE") ?: "Pastoral Announcement"
                val body = intent.getStringExtra("EXTRA_BODY") ?: "New pastoral update from Grace Church"
                val author = intent.getStringExtra("EXTRA_AUTHOR") ?: "Pastoral Office"
                val category = intent.getStringExtra("EXTRA_CATEGORY") ?: "Ministry Update"
                val id = intent.getIntExtra("EXTRA_ID", 1)
                NotificationHelper.sendAnnouncementNotification(
                    context = context,
                    title = title,
                    body = body,
                    author = author,
                    category = category,
                    announcementId = id
                )
            }
        }
    }
}
