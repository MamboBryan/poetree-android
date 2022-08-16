package com.mambo.core.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mambo.core.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationsHelper @Inject constructor(
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

    private val notificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

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

    fun showNotification(title: String, content: String, id: Int, channel: String) {

        val intent = Intent(context, context::class.java)

        intent.data = Uri.parse(context.applicationContext.packageName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val notification = NotificationCompat.Builder(context, channel)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(id, notification.build())
    }

    fun cancelProgressNotification() {
        notificationManager.cancel(ID_UPDATES)
    }

    fun showProgressNotification(title: String, content: String) {

        val intent = Intent()
        intent.data = Uri.parse(context.applicationContext.packageName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_UPDATES)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setProgress(0, 0, true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)

        notificationManager.notify(ID_UPDATES, notification.build())
    }

}