package com.mambo.poetree.ui

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mambo.core.utils.NotificationUtils
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.core.work.InteractReminderWork
import com.mambo.poetree.R
import com.mambo.poetree.databinding.ActivityMainBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.getNavOptionsPopUpTo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment

        navController = navHostFragment.navController

        binding.navBottomMain.setupWithNavController(navController)

//        viewModel.connection.observe(this) { isNetworkAvailable ->
//            binding.layoutConnection.constraintLayoutNetworkStatus.isVisible = !isNetworkAvailable
//        }

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    MainViewModel.MainEvent.SetupDailyInteractionReminder -> setupDailyInteractionReminder()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.darkModeFlow.collect { updateDarkMode(it) }
        }

        setUpDestinationListener()

        initNavigation()

        handleNotificationData()

        showNotification()
    }

    private fun showNotification() {

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(getString(Destinations.PROFILE))

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(this, NotificationUtils.CHANNEL_ID_REMINDER)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("This is It!")
            .setContentText("A small notification to show me what is true")
            .setDefaults(Notification.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

        notificationManager.notify(NotificationUtils.ID_REMINDER, notification.build())
    }

    /**
     * method to handle the data content on clicking of notification if both notification and data payload are sent
     */
    private fun handleNotificationData() {
        val data = intent.data
        if(data != null){
            navController.navigate(data)
        }
    }

    private fun updateDarkMode(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
        delegate.applyDayNight()
    }

    private fun initNavigation() {

        if (!viewModel.isOnBoarded) {
            navigateToOnBoarding()
            return
        }

        if (!viewModel.isLoggedIn) {
            navigateToAuth()
            return
        }

        if (!viewModel.isUserSetup) {
            navigateToSetup()
            return
        }

        navigateToFeeds()

    }

    private fun navigateToOnBoarding() {
        navController.navigate(getDeeplink(Destinations.ON_BOARDING), getLoadingNavOptions())
    }

    private fun navigateToFeeds() {
        navController.navigate(getDeeplink(Destinations.FEED), getLoadingNavOptions())
    }

    private fun navigateToSetup() {
        navController.navigate(getDeeplink(Destinations.SETUP), getLoadingNavOptions())
    }

    private fun navigateToAuth() {
        navController.navigate(getDeeplink(Destinations.LANDING), getLoadingNavOptions())
    }

    private fun getLoadingNavOptions() = getNavOptionsPopUpTo(R.id.flow_loading)

    override fun onBackPressed() {
        when (getDestinationId()) {

            R.id.feedFragment, R.id.landingFragment, R.id.setupFragment -> {
                finish()
            }

            else -> {
                super.onBackPressed()
            }

        }

    }

    private fun setUpDestinationListener() {
        val destinationChangedListener =
            NavController.OnDestinationChangedListener { _: NavController?, destination: NavDestination, _: Bundle? ->
                when (destination.id) {
                    R.id.feedFragment,
                    R.id.exploreFragment,
                    R.id.bookmarksFragment,
                    R.id.libraryFragment -> {
                        showBottomNavigation()
                    }

                    else -> {
                        hideBottomNavigation()
                    }
                }
            }
        navController.addOnDestinationChangedListener(destinationChangedListener)
    }

    private fun getDestinationId(): Int? {
        return navController.currentDestination?.id
    }

    private fun hideBottomNavigation() {
        binding.apply {
            navBottomMain.visibility = View.GONE
        }
    }

    private fun showBottomNavigation() {
        binding.apply {
            navBottomMain.visibility = View.VISIBLE
        }
    }

    private fun setupDailyInteractionReminder() {

        val then = Calendar.getInstance()
        val now = Calendar.getInstance()

        then.set(Calendar.HOUR_OF_DAY, 17)         // set hour
        then.set(Calendar.MINUTE, 15)             // set minute
        then.set(Calendar.SECOND, 0)             // set seconds

        val time = then.timeInMillis - now.timeInMillis

        val work =
            PeriodicWorkRequestBuilder<InteractReminderWork>(24, TimeUnit.HOURS)
                .setInitialDelay(time, TimeUnit.MILLISECONDS)
                .addTag(InteractReminderWork.TAG)
                .build()

        WorkManager.getInstance(this).enqueue(work)

    }
}