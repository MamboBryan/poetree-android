package com.mambo.core.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.messaging.FirebaseMessaging
import com.mambo.core.extensions.isNotNullOrEmpty
import com.mambo.core.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadTokenWork @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: UserRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TAG = "UploadTokenWork"
    }

    override suspend fun doWork(): Result {

        return try {

            var token = ""

            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    Result.retry()
                }

                // Get new FCM registration token
                token = task.result

            }

            if (token.isNotNullOrEmpty())
                repository.updateToken(token)
            else
                Result.retry()

            Result.success()

        } catch (exception: Exception) {
            Log.i(TAG, "Task Failed : ${exception.localizedMessage}")
            Result.failure()
        }
    }

}
