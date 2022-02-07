package com.mambo.core.work

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mambo.core.R
import com.mambo.core.utils.NotificationUtils

class InteractReminderWork(private val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    companion object {
        const val TAG = "Interact Reminder Work"
        private const val NOTIFICATION_ID = NotificationUtils.ID_REMINDER
        private const val NOTIFICATION_CHANNEL = NotificationUtils.CHANNEL_NAME_REMINDER
    }

    override fun doWork(): Result {

        // Do the work here--in this case, upload the images.
        showNotification()

        // Indicate whether the work finished successfully with the Result
        return Result.success()

    }

    private fun showNotification() {


        val intent = Intent()
        intent.data = Uri.parse("com.mambo.poetree")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val titleNotification = "Daily Dose of Art!"
        val subtitleNotification = "How about reading one poem for today ?"

        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(titleNotification)
            .setContentText(subtitleNotification)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(NOTIFICATION_ID, notification.build())

    }
}