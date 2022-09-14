package com.mambo.poetree

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.mambo.core.utils.NetworkMonitoringUtil
import com.mambo.core.utils.NotificationsHelper
import com.mambo.core.utils.ThemeHelper
import com.mambo.data.preferences.UserPreferences
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var notificationsHelper: NotificationsHelper

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var preferences: UserPreferences

    @Inject
    lateinit var networkMonitoringUtil: NetworkMonitoringUtil

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        initTheme()
        plantTimber()
        setupNetworkMonitor()
        setupNotificationChannels()
    }

    private fun setupNetworkMonitor() {
        Timber.i("Network Util called")
        networkMonitoringUtil.register()
        Timber.i("Network Util finished")

    }

    private fun setupNotificationChannels() {
        notificationsHelper.createNotificationChannels()
    }

    private fun initTheme() {

        applicationScope.launch {

            val appTheme = preferences.darkMode
            appTheme.collectLatest { ThemeHelper.applyTheme(it) }

        }
    }

    private fun plantTimber() {
        Timber.plant(Timber.DebugTree())
    }

}