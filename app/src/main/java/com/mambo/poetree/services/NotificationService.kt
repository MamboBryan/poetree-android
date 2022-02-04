package com.mambo.poetree.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mambo.core.utils.NotificationUtils
import com.mambo.core.work.UploadTokenWork
import com.mambo.poetree.R
import com.mambobryan.navigation.Destinations

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

        Log.i("SomeThing", "Notification: " + p0.from);

        val data: MutableMap<String, String> = p0.data
        handleData(data)

    }

    private fun handleData(data: MutableMap<String, String>) {

        val uri: Uri
        val channelID: String
        val notificationID: Int

       if (data.containsKey("type")){
            when (data["type"]) {
                "comment" -> {
                    channelID = NotificationUtils.CHANNEL_ID_COMMENTS
                    notificationID = NotificationUtils.ID_COMMENTS
                    uri = Uri.parse(getString(Destinations.COMMENTS))
                }
                "bookmark" -> {
                    channelID = NotificationUtils.CHANNEL_ID_BOOKMARKS
                    notificationID = NotificationUtils.ID_BOOKMARKS
                    uri = Uri.parse(getString(Destinations.POEM))
                }
                "like" -> {
                    channelID = NotificationUtils.CHANNEL_ID_LIKES
                    notificationID = NotificationUtils.ID_LIKES
                    uri = Uri.parse(getString(Destinations.POEM))
                }
                "poem" -> {
                    channelID = NotificationUtils.CHANNEL_ID_EVENTS
                    notificationID = NotificationUtils.ID_EVENTS
                    uri = Uri.parse(getString(Destinations.POEM))
                }
                else -> {
                    channelID = NotificationUtils.CHANNEL_ID_REMINDER
                    notificationID = NotificationUtils.ID_REMINDER
                    uri = Uri.parse(getString(Destinations.FEED))
                }
            }
        } else {
           channelID = NotificationUtils.CHANNEL_ID_REMINDER
           notificationID = NotificationUtils.ID_REMINDER
           uri = Uri.parse(getString(Destinations.FEED))
        }

        val intent = Intent(Intent.ACTION_VIEW, uri)
        val title = data["title"]!!
        val body = data["body"]!!

        showNotification(title, body, intent, channelID, notificationID)

    }

    private fun showNotification(title: String, body: String, intent: Intent, channelID:String, notificationID: Int) {

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //pass the same channel_id which we created in previous method
        val builder = NotificationCompat.Builder(applicationContext, channelID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setAutoCancel(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        notificationManager.notify(notificationID, builder.build())
    }
}