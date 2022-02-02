package com.mambo.poetree

import android.app.Application
import com.mambo.core.utils.NotificationUtils
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var notificationUtils: NotificationUtils

    override fun onCreate() {
        super.onCreate()

        setupNotificationChannels()

    }

    private fun setupNotificationChannels() {
        notificationUtils.createNotificationChannels()
    }

}