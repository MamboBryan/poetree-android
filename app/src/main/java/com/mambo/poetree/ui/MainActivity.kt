package com.mambo.poetree.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.mambo.core.extensions.isNotNullOrEmpty
import com.mambo.core.utils.IntentExtras
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.core.work.InteractReminderWork
import com.mambo.core.work.UploadTokenWork
import com.mambo.poetree.R
import com.mambo.poetree.databinding.ActivityMainBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.getNavOptionsPopUpTo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment

        navController = navHostFragment.navController

        binding.navBottomMain.setupWithNavController(navController)

        viewModel.connection.observe(this) { isNetworkAvailable ->
            binding.layoutConnection.constraintLayoutNetworkStatus.isVisible = !isNetworkAvailable
        }

        setUpDestinationListener()

        initNavigation()

    }

    /**
     * method to handle the data content on clicking of notification if both notification and data payload are sent
     */
    private fun handleNotificationData() {

        val extras = intent.extras

        if (extras != null) {

            val type = extras.getString(IntentExtras.TYPE)
            val poem = extras.getString(IntentExtras.POEM)

            Timber.i("TYPE: $type ")
            Timber.i("POEM: $poem ")

            val uri: String? =
                if (type != null)
                    when (type) {
                        "comment" -> getString(Destinations.COMMENTS)
                        else -> getString(Destinations.POEM)
                    }
                else null

            if (viewModel.isValidPoem(poem) && uri.isNotNullOrEmpty())
                navController.navigate(Uri.parse(uri))

        } else {
            navigateToFeeds()
        }

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

        handleNotificationData()

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

            R.id.feedFragment,
            R.id.landingFragment,
            R.id.setupFragment -> {
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

}