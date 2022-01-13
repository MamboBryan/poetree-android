package com.mambobryan.features.profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mambobryan.features.profile.databinding.FragmentProfileBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.navigate
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val binding by viewBinding(FragmentProfileBinding::bind)
    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    ProfileViewModel.ProfileEvent.NavigateToPrivacyPolicy -> navigateToPrivacyPolicy()
                    ProfileViewModel.ProfileEvent.NavigateToTermsAndConditions -> navigateToTermsAndConditions()
                    ProfileViewModel.ProfileEvent.NavigateToUpdateAccount -> navigateToUpdateAccount()
                    ProfileViewModel.ProfileEvent.NavigateToUpdatePassword -> navigateToUpdatePassword()
                    ProfileViewModel.ProfileEvent.ShowAppThemeDialog -> openAppThemeDialog()
                }
            }
        }

    }

    private fun initViews() = binding.apply {
        NavigationUI.setupWithNavController(toolbar, findNavController())
        toolbar.title = "Profile"

        ivAppTheme.setOnClickListener { viewModel.onAppThemeClicked() }
        tvProfileEdit.setOnClickListener { viewModel.onUpdateAccountClicked() }
        tvProfilePassword.setOnClickListener { viewModel.onUpdatePasswordClicked() }
        tvProfileTerms.setOnClickListener { viewModel.onTermsAndConditionsClicked() }
        tvProfilePrivacy.setOnClickListener { viewModel.onPrivacyPolicyClicked() }

    }

    private fun openAppThemeDialog() {

        val modes = listOf(
            AppCompatDelegate.MODE_NIGHT_YES,
            AppCompatDelegate.MODE_NIGHT_NO,
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )

        val mode = viewModel.mode.value
        val titles = arrayOf("On", "Off", "Use device settings")

        val position = when (mode) {
            AppCompatDelegate.MODE_NIGHT_YES -> 0
            AppCompatDelegate.MODE_NIGHT_NO -> 1
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> 2
            else -> 2
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Dark Mode")
            .setSingleChoiceItems(titles, position) { p0, p1 ->
                viewModel.onAppThemeSelected(modes[p1])
            }
            .show()

    }

    private fun navigateToUpdateAccount() {
        navigate(getDeeplink(Destinations.ACCOUNT_EDIT))
    }

    private fun navigateToUpdatePassword() {
        navigate(getDeeplink(Destinations.UPDATE_PASSWORD))
    }

    private fun navigateToPrivacyPolicy() {}

    private fun navigateToTermsAndConditions() {}
}