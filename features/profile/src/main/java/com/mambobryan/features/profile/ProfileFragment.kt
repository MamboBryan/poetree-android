package com.mambobryan.features.profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.irozon.alertview.AlertActionStyle
import com.irozon.alertview.AlertStyle
import com.irozon.alertview.AlertTheme
import com.irozon.alertview.AlertView
import com.irozon.alertview.objects.AlertAction
import com.mambo.core.utils.LoadingDialog
import com.mambobryan.features.profile.databinding.FragmentProfileBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.getNavOptionsPopUpToCurrent
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
                    ProfileViewModel.ProfileEvent.NavigateToLanding -> navigateToLanding()
                    ProfileViewModel.ProfileEvent.ShowDarkModeDialog -> showDarkModeDialog()
                    ProfileViewModel.ProfileEvent.ShowLogOutDialog -> showLogoutDialog()
                    ProfileViewModel.ProfileEvent.HideLoadingDialog -> {
                        LoadingDialog.dismiss()
                    }
                    ProfileViewModel.ProfileEvent.ShowLoadingDialog -> {
                        LoadingDialog.show(requireContext(), false)
                    }
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
        btnProfileLogOut.setOnClickListener { viewModel.onLogOutClicked() }

    }

    private fun showLogoutDialog() {
        val alert = AlertView(
            "Logging Out",
            "We're going to miss you! \n Are you sure you want to logout?",
            AlertStyle.BOTTOM_SHEET
        )
        alert.addAction(
            AlertAction("Yes", AlertActionStyle.NEGATIVE) { viewModel.onLogOutConfirmed() }
        )
        alert.show(requireActivity() as AppCompatActivity)
    }

    private fun showDarkModeDialog() {

        val modes = listOf(
            AppCompatDelegate.MODE_NIGHT_NO,
            AppCompatDelegate.MODE_NIGHT_YES,
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )

        val isDark = viewModel.mode == modes[1] || viewModel.mode == modes[2]

        val alert = AlertView("Choose Theme", "", AlertStyle.BOTTOM_SHEET)
        alert.addAction(
            AlertAction("Light", AlertActionStyle.DEFAULT) {
                viewModel.onDarkModeSelected(modes[0])
            }
        )
        alert.addAction(
            AlertAction("Dark", AlertActionStyle.DEFAULT) {
                viewModel.onDarkModeSelected(modes[1])
            }
        )
        alert.addAction(
            AlertAction("System Default", AlertActionStyle.DEFAULT) {
                viewModel.onDarkModeSelected(modes[2])
            }
        )
        alert.setTheme(if (isDark) AlertTheme.DARK else AlertTheme.LIGHT)
        alert.show(requireActivity() as AppCompatActivity)

    }

    private fun navigateToUpdateAccount() {
        navigate(getDeeplink(Destinations.ACCOUNT_EDIT))
    }

    private fun navigateToUpdatePassword() {
        navigate(getDeeplink(Destinations.UPDATE_PASSWORD))
    }

    private fun navigateToLanding() {
        navigate(getDeeplink(Destinations.LANDING), getNavOptionsPopUpToCurrent())
    }

    private fun navigateToPrivacyPolicy() {}

    private fun navigateToTermsAndConditions() {}
}