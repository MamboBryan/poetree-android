package com.mambo.poetree.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.mambo.poetree.R
import com.mambo.poetree.databinding.ActivityMainBinding
import com.mambo.poetree.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val viewModel by viewModels<MainViewModel>()

    private var backIsAlreadyPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
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

    }

    override fun onBackPressed() {

        when (getDestinationId()) {

            R.id.onBoardingFragment,
            R.id.authenticationFragment,
            R.id.homeFragment -> {

                if (backIsAlreadyPressed) {
                    finish()
                } else {
                    backIsAlreadyPressed = true

                    Toast.makeText(this, "Press \"BACK\" again to exit", Toast.LENGTH_SHORT)
                        .show()

                    Handler(Looper.getMainLooper())
                        .postDelayed(
                            { backIsAlreadyPressed = false }, 2000
                        )
                }

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
                    R.id.dashboardFragment,
                    R.id.libraryFragment,
                    R.id.accountFragment,
                    R.id.discoverFragment,
                    -> {
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
        // Check if the current destination is actually the start destination (Home screen)
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