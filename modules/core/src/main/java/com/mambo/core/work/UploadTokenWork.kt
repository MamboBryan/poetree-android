package com.mambo.core.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.mambo.core.extensions.isNotNullOrEmpty
import com.mambo.core.repository.UserRepository
import com.mambo.data.preferences.UserPreferences
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

@HiltWorker
class UploadTokenWork @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: UserRepository,
    private val preferences: UserPreferences
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        fun scheduleWork(context: Context) {

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val worker = OneTimeWorkRequestBuilder<UploadImageWork>()
                .setConstraints(constraints)
                .build()

            WorkManager
                .getInstance(context)
                .enqueueUniqueWork(UploadImageWork.toString(), ExistingWorkPolicy.REPLACE, worker)

        }
    }

    override suspend fun doWork(): Result {

        return try {

            val signedIn = preferences.isLoggedIn.first()

            if (signedIn.not()) return Result.failure()

            val token = Firebase.messaging.token.await()

            if (token.isNullOrBlank()) return Result.failure()

            val response = repository.updateToken(token)

            if (response.isSuccessful.not())
                Result.failure()

            Result.success()

        } catch (exception: Exception) {
            Result.failure()
        }
    }

}
