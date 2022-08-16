package com.mambo.core.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationUtils @Inject constructor(
    @ApplicationContext val context: Context
) {

    companion object {
        const val ID_REMINDER = 1000
        const val CHANNEL_ID_REMINDER = "reminders"
        const val CHANNEL_NAME_REMINDER = "Reminders"

        const val ID_BOOKMARKS = 2000
        const val CHANNEL_ID_BOOKMARKS = "bookmarks"
        const val CHANNEL_NAME_BOOKMARKS = "Bookmarks"

        const val ID_EVENTS = 3000
        const val CHANNEL_ID_EVENTS = "events"
        const val CHANNEL_NAME_EVENTS = "Events"

        const val ID_LIKES = 4000
        const val CHANNEL_ID_LIKES = "likes"
        const val CHANNEL_NAME_LIKES = "Likes"

        const val ID_COMMENTS = 5000
        const val CHANNEL_ID_COMMENTS = "comments"
        const val CHANNEL_NAME_COMMENTS = "Comments"

        const val ID_UPDATES = 6000
        const val CHANNEL_ID_UPDATES = "updates"
        const val CHANNEL_NAME_UPDATES = "Updates"
    }

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channels = listOf(
                NotificationChannel(
                  CHANNEL_ID_LIKES, CHANNEL_NAME_LIKES, NotificationManager.IMPORTANCE_HIGH
                ),
                NotificationChannel(
                    CHANNEL_ID_COMMENTS, CHANNEL_NAME_COMMENTS, NotificationManager.IMPORTANCE_HIGH
                ),
                NotificationChannel(
                    CHANNEL_ID_BOOKMARKS, CHANNEL_NAME_BOOKMARKS, NotificationManager.IMPORTANCE_HIGH
                ),
                NotificationChannel(
                    CHANNEL_ID_REMINDER, CHANNEL_NAME_REMINDER, NotificationManager.IMPORTANCE_DEFAULT
                ),
                NotificationChannel(
                    CHANNEL_ID_UPDATES, CHANNEL_NAME_UPDATES, NotificationManager.IMPORTANCE_DEFAULT
                ),
                NotificationChannel(
                    CHANNEL_ID_EVENTS, CHANNEL_NAME_EVENTS, NotificationManager.IMPORTANCE_LOW
                )
            )

            val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannels(channels)
        }
    }

    fun showNotification(title: String, content: String) {

        val intent = Intent()
        intent.data = Uri.parse(context.applicationContext.packageName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_GENERAL)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(content)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(ID_GENERAL, notification.build())
    }

    fun cancelProgressNotification() {
        notificationManager.cancel(ID_SYNC)
    }

    fun showProgressNotification(title: String, content: String) {

        val intent = Intent()
        intent.data = Uri.parse(context.applicationContext.packageName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_SYNC)
            .setSmallIcon(R.drawable.ic_baseline_sync_24)
            .setContentTitle(title)
            .setContentText(content)
            .setDefaults(Notification.DEFAULT_ALL)
            .setProgress(0, 0, true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)

        notificationManager.notify(ID_SYNC, notification.build())
    }

}