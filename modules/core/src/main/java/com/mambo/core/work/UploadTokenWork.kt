package com.mambo.core.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.mambo.core.extensions.isNotNullOrEmpty
import com.mambo.core.repository.UserRepositoryUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class UploadTokenWork @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: UserRepositoryUseCase
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TAG = "UploadTokenWork"
    }

    override suspend fun doWork(): Result {

        return try {

            val token = Firebase.messaging.token.await()

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
