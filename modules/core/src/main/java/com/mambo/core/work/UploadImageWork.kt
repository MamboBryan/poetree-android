package com.mambo.core.work

import android.content.Context
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.mambo.core.repository.ImageRepository
import com.mambo.core.repository.UserRepository
import com.mambo.core.utils.Constants
import com.mambo.core.utils.NotificationsHelper
import com.mambo.data.preferences.UserPreferences
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
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
    private val imageRepository: ImageRepository,
    private val userRepository: UserRepository,
    private val preferences: UserPreferences,
    private val notificationsHelper: NotificationsHelper
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val WORK_TAG = "Upload Image Work"

        fun scheduleWork(context: Context, data: Data) {

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val updateWorker = OneTimeWorkRequestBuilder<UploadImageWork>()
                .setConstraints(constraints)
                .setInputData(data)
                .build()

            WorkManager
                .getInstance(context)
                .enqueueUniqueWork(WORK_TAG, ExistingWorkPolicy.REPLACE, updateWorker)

        }
    }

    override suspend fun doWork(): Result {

        try {

            val user = preferences.user.firstOrNull() ?: return Result.failure()

            val userId = user.id
            val imageString = inputData.getString(Constants.KEY_MEDIA_URI)

            if (userId.isNullOrBlank() || imageString.isNullOrEmpty()) return Result.failure()

            val imageUri = imageString.toUri()

            notificationsHelper.showProgressNotification("Uploading profile image", "")

            val task = imageRepository.upload(userId.toString(), imageUri).metadata?.reference

            if (task == null) {
                cancelNotification()
                return Result.retry()
            }

            val url = task.downloadUrl.await()

            preferences.updateImageUrl(url.toString())

            val response = userRepository.updateImageUrl(url.toString())

            if (response.isSuccessful.not()) {
                cancelNotification()
                return Result.retry()
            }

            return Result.success().also { notificationsHelper.cancelProgressNotification() }

        } catch (exception: Exception) {
            return Result.failure()
        }
    }

    private fun cancelNotification() {
        notificationsHelper.cancelProgressNotification()
    }

}