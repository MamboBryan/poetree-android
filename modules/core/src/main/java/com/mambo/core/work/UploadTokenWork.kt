package com.mambo.core.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.messaging.FirebaseMessaging
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

            val task = FirebaseMessaging.getInstance().token

            if (!task.isSuccessful) {
                Log.i(TAG, "doWork: ${task.exception?.message}")
                Result.retry()
            }

            val token = task.result!!

            repository.updateToken(token)

            Result.success()

        } catch (exception: Exception) {
            Log.i(TAG, "doWork: ${exception.message}")
            Result.failure()
        }
    }

}
