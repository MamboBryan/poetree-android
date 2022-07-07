package com.mambo.core.work

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.mambo.core.R
import com.mambo.core.repository.ImageRepository
import com.mambo.core.repository.UserRepositoryUseCase
import com.mambo.core.utils.Constants
import com.mambo.core.utils.NotificationUtils
import com.mambo.data.preferences.UserPreferences
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

/**
 * Poetree
 * @author Mambo Bryan
 * @email mambobryan@gmail.com
 * Created 4/17/22 at 10:55 PM
 */
@HiltWorker
class UploadImageWork @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: ImageRepository,
    private val userUseCase: UserRepositoryUseCase,
    private val preferences: UserPreferences
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val WORK_TAG = "Upload Image Work"
        private const val NOTIFICATION_ID = NotificationUtils.ID_UPDATES
        private const val NOTIFICATION_CHANNEL = NotificationUtils.CHANNEL_ID_UPDATES

        fun scheduleWork(context: Context) {

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val updateWorker = OneTimeWorkRequestBuilder<UploadImageWork>()
                .setConstraints(constraints)
                .build()

            WorkManager
                .getInstance(context)
                .enqueueUniqueWork(WORK_TAG, ExistingWorkPolicy.REPLACE, updateWorker)

        }
    }

    override suspend fun doWork(): Result {

        try {

            val userId = inputData.getInt(Constants.KEY_MEDIA_ID, -1)
            val imageString = inputData.getString(Constants.KEY_MEDIA_URI)

            if (userId == -1 || imageString.isNullOrEmpty() ) return Result.failure()

            val imageUri = imageString.toUri()

            showUploadNotification()

            val task =
                repository.upload(userId.toString(), imageUri).metadata?.reference
                    ?: return Result.failure()

            val url = task.downloadUrl.await()

            preferences.updateImageUrl(url.toString())

            val response = userUseCase.updateImageUrl(url.toString())

            if (false) return Result.failure()

            return Result.success()

        } catch (exception: Exception) {
            return Result.failure()
        }
    }

    private fun showUploadNotification() {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Build a notification
        val notification = NotificationCompat.Builder(appContext, NOTIFICATION_CHANNEL)
            .setContentTitle("Uploading image ...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()

        //A notification HAS to be passed for the foreground service to be started.
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

}