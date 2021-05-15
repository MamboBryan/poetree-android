package com.mambo.poetree.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentLandingBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class LandingFragment : Fragment(R.layout.fragment_landing) {

    private val binding by viewBinding(FragmentLandingBinding::bind)

    private val userIsLoggedIn = false
    private val userHasSetUp = true

    private val isFirstTimeOpeningApp = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        dummy process to show loading
         */
        Handler(Looper.getMainLooper()).postDelayed({

            when {
                isFirstTimeOpeningApp -> {
                    navigateToOnBoarding()
                }
                else -> {
                    if (userIsLoggedIn) {

                        if (userHasSetUp)
                            navigateToDashboard()
                        else
                            navigateToSetup()

                    } else {
                        navigateToAuthentication()
                    }
                }
            }

        }, 2000)

        binding.apply {

        }
    }

    private fun navigateToSetup() {
        setStartDestination(R.id.setupFragment)
        findNavController().navigate(R.id.action_landingFragment_to_setupFragment)
    }

    private fun navigateToOnBoarding() {
        setStartDestination(R.id.onBoardingFragment)

        val action = LandingFragmentDirections.actionLandingFragmentToOnBoardingFragment()
        navigate(action)
    }

    private fun navigateToDashboard() {
        setStartDestination(R.id.dashboardFragment)

        val action = LandingFragmentDirections.actionLandingFragmentToDashboardFragment()
        navigate(action)
    }

    private fun navigateToAuthentication() {
        setStartDestination(R.id.authenticationFragment)

        val action = LandingFragmentDirections.actionLandingFragmentToAuthenticationFragment()
        navigate(action)

    }

    private fun navigate(action: NavDirections) {
        findNavController().navigate(action)
    }

    private fun setStartDestination(id: Int) {
        findNavController().graph.startDestination = id
    }
}