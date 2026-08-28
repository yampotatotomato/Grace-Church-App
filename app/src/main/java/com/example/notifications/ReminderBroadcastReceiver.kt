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
        }
    }
}
