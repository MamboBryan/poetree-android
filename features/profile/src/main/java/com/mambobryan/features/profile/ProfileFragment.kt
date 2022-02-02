package com.mambobryan.features.profile

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.irozon.alertview.AlertActionStyle
import com.irozon.alertview.AlertStyle
import com.irozon.alertview.AlertTheme
import com.irozon.alertview.AlertView
import com.irozon.alertview.objects.AlertAction
import com.mambo.core.utils.LoadingDialog
import com.mambo.core.utils.prettyCount
import com.mambobryan.features.profile.databinding.FragmentProfileBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.getNavOptionsPopUpToCurrent
import com.mambobryan.navigation.extensions.navigate
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlin.math.abs


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
                    ProfileViewModel.ProfileEvent.NavigateToPrivacyPolicy -> openRepoViaLink()
                    ProfileViewModel.ProfileEvent.NavigateToTermsAndConditions -> openRepoViaLink()
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

        setupUserView()
        setUpScrollView()

        layoutProfileHeader.ivHeaderDarkMode.setOnClickListener { viewModel.onAppThemeClicked() }
        layoutProfileHeader.ivHeaderBack.setOnClickListener { findNavController().popBackStack() }
        tvProfileEdit.setOnClickListener { viewModel.onUpdateAccountClicked() }
        tvProfilePassword.setOnClickListener { viewModel.onUpdatePasswordClicked() }
        tvProfileTerms.setOnClickListener { viewModel.onTermsAndConditionsClicked() }
        tvProfilePrivacy.setOnClickListener { viewModel.onPrivacyPolicyClicked() }
        btnProfileLogOut.setOnClickListener { viewModel.onLogOutClicked() }

    }

    private fun setupUserView() = binding.apply {
//        val user = sharedViewModel.user.value
        layoutProfileHeader.tvHeaderTitle.text = "MamboBryan"
        layoutProfileDetails.apply {
            tvArtistName.text = "MamboBryan"
//            tvArtistBio.text =""
            tvArtistReads.text = prettyCount(2000000)
            tvArtistBookmarks.text = prettyCount(20000)
            tvArtistLikes.text = prettyCount(200000)
        }

        try {
            val info =
                requireActivity().packageManager?.getPackageInfo(requireActivity().packageName, 0)
            val versionName = info?.versionName
            tvSettingsVersion.text = "Poetree for Android v$versionName"

        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("Settings", "setupUserView: ${e.localizedMessage}")
        }
    }

    private fun setUpScrollView() = binding.apply {
        layoutProfileHeader.ivHeaderSplit.visibility = View.GONE
        layoutProfileHeader.tvHeaderTitle.translationY = -1000f
        layoutScrollRoot.setOnScrollListener { ty, _ ->

            var titleMaxScrollHeight = 0f
            var headerMaxHeight = 0f
            var avatarTop = 0f
            var maxScrollHeight = 0f

            var translationY = ty
            translationY = -translationY

            val tvTitle = layoutProfileHeader.tvHeaderTitle
            val tvName = layoutProfileDetails.tvArtistName
            val ivArtistImage = layoutProfileDetails.ivArtistMage
            val ivSplit = layoutProfileHeader.ivHeaderSplit

            if (titleMaxScrollHeight == 0f) {
                titleMaxScrollHeight = ((tvTitle.parent as View).bottom - tvTitle.top).toFloat()
                maxScrollHeight = headerMaxHeight + titleMaxScrollHeight
            }

            if (headerMaxHeight == 0f) {
                headerMaxHeight = tvName.top.toFloat()
                maxScrollHeight = headerMaxHeight + titleMaxScrollHeight
            }

            if (avatarTop == 0f) {
                avatarTop = ivArtistImage.top.toFloat()
            }

            var alpha = 0
            val baseAlpha = 60
            if (0 > avatarTop + translationY) {
                alpha =
                    255.coerceAtMost((abs(avatarTop + translationY) * (255 - baseAlpha) / (headerMaxHeight - avatarTop) + baseAlpha).toInt())
                ivSplit.visibility = View.VISIBLE
            } else {
                ivSplit.visibility = View.GONE
            }
            ivSplit.background.alpha = alpha
            tvTitle.translationY =
                (0.coerceAtLeast(maxScrollHeight.toInt() + translationY)).toFloat()
        }
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

    private fun openRepoViaLink() {
        val url = "https://github.com/MamboBryan/poetree"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

}