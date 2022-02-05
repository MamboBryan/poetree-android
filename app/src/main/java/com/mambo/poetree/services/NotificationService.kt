package com.mambo.poetree.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mambo.core.extensions.isNotNull
import com.mambo.core.utils.IntentExtras
import com.mambo.core.utils.NotificationUtils
import com.mambo.core.utils.Type
import com.mambo.core.work.UploadTokenWork
import com.mambo.poetree.R
import com.mambo.poetree.ui.MainActivity

class NotificationService : FirebaseMessagingService() {

    companion object {
        const val TAG = "NotificationService"
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        startUpdateTokenWork()

    }

    private fun startUpdateTokenWork() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val work =
            OneTimeWorkRequestBuilder<UploadTokenWork>()
                .addTag(UploadTokenWork.TAG)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(this).enqueue(work)
    }


    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        val notification = p0.notification
        val data = p0.data

        handleData(notification, data)

    }

    private fun handleData(
        notification: RemoteMessage.Notification?,
        data: MutableMap<String, String>
    ) {
        val intent = Intent(this, MainActivity::class.java)

        val channelID: String
        val notificationID: Int

        val poem = data[IntentExtras.POEM]
        val type = data[IntentExtras.TYPE]

        val title = notification?.title ?: "Woot! Woot!"
        val body = notification?.body ?: "Something happened but I don't know what it is"

        if (type.isNotNull()) {
            when (type) {
                Type.COMMENT -> {
                    channelID = NotificationUtils.CHANNEL_ID_COMMENTS
                    notificationID = NotificationUtils.ID_COMMENTS
                }
                Type.BOOKMARK -> {
                    channelID = NotificationUtils.CHANNEL_ID_BOOKMARKS
                    notificationID = NotificationUtils.ID_BOOKMARKS
                }
                Type.LIKE -> {
                    channelID = NotificationUtils.CHANNEL_ID_LIKES
                    notificationID = NotificationUtils.ID_LIKES
                }
                Type.POEM -> {
                    channelID = NotificationUtils.CHANNEL_ID_EVENTS
                    notificationID = NotificationUtils.ID_EVENTS
                }
                else -> {
                    channelID = NotificationUtils.CHANNEL_ID_REMINDER
                    notificationID = NotificationUtils.ID_REMINDER
                }
            }
        } else {
            channelID = NotificationUtils.CHANNEL_ID_REMINDER
            notificationID = NotificationUtils.ID_REMINDER
        }

        intent.putExtra(IntentExtras.POEM, poem)
        intent.putExtra(IntentExtras.TYPE, type)

        showNotification(title, body, intent, channelID, notificationID)

    }

    private fun showNotification(
        title: String,
        body: String,
        intent: Intent,
        channelID: String,
        notificationID: Int
    ) {

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(applicationContext, channelID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setAutoCancel(true)
            .setSound(null)
            .setContentIntent(
                PendingIntent.getActivity(
                    this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

        notificationManager.notify(notificationID, builder.build())
    }
}