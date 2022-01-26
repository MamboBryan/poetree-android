package com.mambo.poetree.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.poetree.R
import com.mambo.poetree.databinding.ActivityMainBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.getNavOptionsPopUpTo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel by viewModels<MainViewModel>()

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

        setUpDestinationListener()

        initNavigation()

        lifecycleScope.launchWhenStarted {
            viewModel.darkMode.collect { updateDarkMode(it) }
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

        navigateToHome()

    }

    private fun navigateToOnBoarding() {
        navController.navigate(getDeeplink(Destinations.ON_BOARDING), getLoadingNavOptions())
    }

    private fun navigateToHome() {
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

            R.id.feedFragment -> {

                if (viewModel.backIsPressed) {
                    finish()
                } else {
                    viewModel.backIsPressed = true

                    Toast.makeText(this, "Press \"BACK\" again to exit", Toast.LENGTH_SHORT)
                        .show()

                    Handler(Looper.getMainLooper())
                        .postDelayed({ viewModel.backIsPressed = false }, 2000)
                }

            }

            else -> {
                super.onBackPressed()
            }

        }

    }

    private fun updateDarkMode(mode: Int) {

        AppCompatDelegate.setDefaultNightMode(mode)
        delegate.applyDayNight()

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