package com.mambo.poetree.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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

        setUpDestinationListener()

        initNavigation()

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
}