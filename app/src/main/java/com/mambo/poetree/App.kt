package com.mambo.poetree

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.mambo.core.utils.NotificationUtils
import com.mambo.core.utils.ThemeHelper
import com.mambo.data.preferences.UserPreferences
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var notificationUtils: NotificationUtils

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var preferences: UserPreferences
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        Firebase.messaging.subscribeToTopic("featured")
        setupNotificationChannels()
        initTheme()
    }

    private fun setupNotificationChannels() {
        notificationUtils.createNotificationChannels()
    }

    private fun initTheme() {

        applicationScope.launch {

            val appTheme = preferences.darkMode
            appTheme.collectLatest { ThemeHelper.applyTheme(it) }

        }
    }

}