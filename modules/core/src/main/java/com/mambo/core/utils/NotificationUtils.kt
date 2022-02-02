package com.mambo.core.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationUtils @Inject constructor(
    @ApplicationContext val context: Context
) {

    companion object {
        const val ID_REMINDER = 1000
        const val CHANNEL_ID_REMINDER = "Reminders"
        const val CHANNEL_NAME_REMINDER = "Reminders"

        const val ID_UPDATES = 2000
        const val CHANNEL_ID_UPDATES = "updates"
        const val CHANNEL_NAME_UPDATES = "Updates"

        const val ID_EVENTS = 3000
        const val CHANNEL_ID_EVENTS = "events"
        const val CHANNEL_NAME_EVENTS = "Events"

        const val ID_LIKES = 4000
        const val CHANNEL_ID_LIKES = "likes"
        const val CHANNEL_NAME_LIKES = "Likes"

        const val ID_COMMENTS = 5000
        const val CHANNEL_ID_COMMENTS = "comments"
        const val CHANNEL_NAME_COMMENTS = "Comments"
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
                    CHANNEL_ID_REMINDER, CHANNEL_NAME_REMINDER, NotificationManager.IMPORTANCE_HIGH
                ),
                NotificationChannel(
                    CHANNEL_ID_UPDATES, CHANNEL_NAME_UPDATES, NotificationManager.IMPORTANCE_DEFAULT
                ),
                NotificationChannel(
                    CHANNEL_ID_EVENTS, CHANNEL_NAME_EVENTS, NotificationManager.IMPORTANCE_DEFAULT
                )
            )

            val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannels(channels)
        }
    }

}