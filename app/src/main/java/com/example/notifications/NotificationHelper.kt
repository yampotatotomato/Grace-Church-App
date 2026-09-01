package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.DailyVerse
import com.example.data.model.PrayerGroup

object NotificationHelper {

    const val CHANNEL_DAILY_VERSE = "channel_daily_verse"
    const val CHANNEL_MEETINGS = "channel_meetings"
    const val CHANNEL_COMMUNITY = "channel_community"
    const val CHANNEL_ANNOUNCEMENTS = "channel_announcements"

    const val NOTIF_ID_VERSE = 1001
    const val NOTIF_ID_MEETING = 1002
    const val NOTIF_ID_COMMUNITY = 1003
    const val NOTIF_ID_ANNOUNCEMENT_BASE = 2000

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val verseChannel = NotificationChannel(
                CHANNEL_DAILY_VERSE,
                "Daily Scripture & Verse of the Day",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily inspirational scripture verses and morning reflections"
                enableVibration(true)
            }

            val meetingChannel = NotificationChannel(
                CHANNEL_MEETINGS,
                "Prayer Group & Service Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for upcoming prayer groups, Bible studies, and church services"
                enableVibration(true)
            }

            val communityChannel = NotificationChannel(
                CHANNEL_COMMUNITY,
                "Pastoral Notes & Community Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Pastoral message confirmations and community prayer updates"
            }

            val announcementChannel = NotificationChannel(
                CHANNEL_ANNOUNCEMENTS,
                "Church Announcements & Pastoral Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent church announcements, pastoral letters, and scheduled ministry updates"
                enableVibration(true)
            }

            manager.createNotificationChannel(verseChannel)
            manager.createNotificationChannel(meetingChannel)
            manager.createNotificationChannel(communityChannel)
            manager.createNotificationChannel(announcementChannel)
        }
    }

    fun sendDailyVerseNotification(context: Context, verse: DailyVerse) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("EXTRA_NAV_TARGET", "scripture")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_DAILY_VERSE)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Verse of the Day • ${verse.reference}")
            .setContentText("\"${verse.text}\"")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("\"${verse.text}\"\n\n— ${verse.reference}\nTheme: ${verse.theme}")
                    .setSummaryText("Grace Church Daily Bread")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIF_ID_VERSE, notification)
        } catch (e: SecurityException) {
            // Android 13+ permission not granted yet
        }
    }

    fun sendMeetingReminderNotification(context: Context, group: PrayerGroup) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("EXTRA_NAV_TARGET", "prayer_groups")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_MEETINGS)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Upcoming Meeting: ${group.name}")
            .setContentText("Meeting ${group.meetingDayTime} at ${group.locationName}")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Reminder for ${group.name}!\n\n🕒 Time: ${group.meetingDayTime}\n📍 Location: ${group.locationName} (${group.address})\nLeader: ${group.leaderName}")
                    .setSummaryText("Prayer Group Reminder")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIF_ID_MEETING, notification)
        } catch (e: SecurityException) {
            // Android 13+ permission not granted
        }
    }

    fun sendPastorResponseNotification(context: Context, pastorName: String, preview: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("EXTRA_NAV_TARGET", "pastors")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_COMMUNITY)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("Guidance Request Sent to $pastorName")
            .setContentText("Your request was received by the pastoral office.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Your guidance request was submitted to $pastorName.\n\n\"$preview\"\n\nThe pastoral office will provide biblical counsel and prayer.")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIF_ID_COMMUNITY, notification)
        } catch (e: SecurityException) {
            // Permission handling
        }
    }

    fun sendPastorGuidanceRepliedNotification(context: Context, pastorName: String, preview: String, scripture: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("EXTRA_NAV_TARGET", "pastors")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            3,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_COMMUNITY)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("New Guidance from $pastorName")
            .setContentText("$pastorName responded to your guidance request: \"$preview\"")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$pastorName sent you biblical guidance & prayer:\n\n\"$preview\"\n\n📖 Scripture Focus: $scripture")
                    .setSummaryText("Pastoral Guidance")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIF_ID_COMMUNITY, notification)
        } catch (e: SecurityException) {
            // Permission handling
        }
    }

    fun sendAnnouncementNotification(
        context: Context,
        title: String,
        body: String,
        author: String,
        category: String,
        announcementId: Int
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("EXTRA_NAV_TARGET", "announcements")
            putExtra("EXTRA_ANNOUNCEMENT_ID", announcementId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIF_ID_ANNOUNCEMENT_BASE + announcementId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val displayTitle = if (title.isNotBlank()) title else "Pastoral Announcement"
        val notification = NotificationCompat.Builder(context, CHANNEL_ANNOUNCEMENTS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("📢 $displayTitle")
            .setContentText(body)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$body\n\n— $author ($category)")
                    .setSummaryText("Grace Church Announcement")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                NOTIF_ID_ANNOUNCEMENT_BASE + (announcementId % 1000),
                notification
            )
        } catch (e: SecurityException) {
            // Permission handling
        }
    }
}
