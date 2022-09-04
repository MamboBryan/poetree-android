package com.mambobryan.features.settings

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.irozon.alertview.AlertActionStyle
import com.irozon.alertview.AlertStyle
import com.irozon.alertview.AlertView
import com.irozon.alertview.objects.AlertAction
import com.mambo.core.utils.LoadingDialog
import com.mambobryan.features.settings.databinding.FragmentSettingsBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.navigate
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding by viewBinding(FragmentSettingsBinding::bind)
    private val viewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    SettingsViewModel.SettingsEvent.NavigateToPrivacyPolicy -> openRepoViaLink()
                    SettingsViewModel.SettingsEvent.NavigateToTermsAndConditions -> openRepoViaLink()
                    SettingsViewModel.SettingsEvent.NavigateToUpdateAccount -> navigateToUpdateAccount()
                    SettingsViewModel.SettingsEvent.NavigateToUpdatePassword -> navigateToUpdatePassword()
                    SettingsViewModel.SettingsEvent.NavigateToLanding -> navigateToLanding()
                    SettingsViewModel.SettingsEvent.ShowLogOutDialog -> showLogoutDialog()
                    SettingsViewModel.SettingsEvent.ShowDeleteAccountDialog -> showDeleteAccountDialog()
                    SettingsViewModel.SettingsEvent.HideLoadingDialog -> {
                        LoadingDialog.dismiss()
                    }
                    SettingsViewModel.SettingsEvent.ShowLoadingDialog -> {
                        LoadingDialog.show(requireContext())
                    }
                }
            }
        }

    }

    private fun initViews() = binding.apply {

        NavigationUI.setupWithNavController(toolbarSettings, findNavController())
        toolbarSettings.title = "Settings"

        tvSettingsEdit.setOnClickListener { viewModel.onUpdateAccountClicked() }
        tvSettingsPassword.setOnClickListener { viewModel.onUpdatePasswordClicked() }
        tvSettingsTerms.setOnClickListener { viewModel.onTermsAndConditionsClicked() }
        tvSettingsPrivacy.setOnClickListener { viewModel.onPrivacyPolicyClicked() }
        btnSettingsDelete.setOnClickListener { viewModel.onDeleteAccountClicked() }
        btnSettingsLogOut.setOnClickListener { viewModel.onLogOutClicked() }

        try {
            val info =
                requireActivity().packageManager?.getPackageInfo(requireActivity().packageName, 0)
            val versionName = info?.versionName
            val versionCode = info?.versionCode
            tvSettingsVersion.text = "Poetree for Android v$versionName ( $versionCode )"

        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e("Version Name: ${e.localizedMessage}")
        }
    }

    private fun showLogoutDialog() {
        val alert = AlertView(
            "Logging Out",
            "You're going to lose all your saved poems and We're going to miss you! \n Are you sure you want to logout?",
            AlertStyle.DIALOG
        )
        alert.addAction(
            AlertAction("Yes", AlertActionStyle.NEGATIVE) {
                viewModel.onLogOutConfirmed()
            }
        )
        alert.show(requireActivity() as AppCompatActivity)
    }

    private fun showDeleteAccountDialog() {
        val alert = AlertView(
            "Delete Account",
            "You're about to delete your account! \n This process cannot be reversed.",
            AlertStyle.DIALOG
        )
        alert.addAction(
            AlertAction("Confirm", AlertActionStyle.NEGATIVE) {
                viewModel.onDeleteAccountConfirmed()
            }
        )
        alert.show(requireActivity() as AppCompatActivity)
    }

    private fun navigateToUpdateAccount() {
        navigate(getDeeplink(Destinations.ACCOUNT_EDIT))
    }

    private fun navigateToUpdatePassword() {
        navigate(getDeeplink(Destinations.UPDATE_PASSWORD))
    }

    private fun navigateToLanding() {

        val intent = requireActivity().intent
        requireActivity().finishAffinity().also {
            startActivity(intent)
        }

    }

    private fun openRepoViaLink() {
        val url = "https://github.com/MamboBryan/poetree"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

}