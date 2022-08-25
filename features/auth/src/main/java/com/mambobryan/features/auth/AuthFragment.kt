package com.mambobryan.features.auth

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.irozon.alertview.AlertActionStyle
import com.irozon.alertview.AlertStyle
import com.irozon.alertview.AlertView
import com.irozon.alertview.objects.AlertAction
import com.mambo.core.adapters.ViewPagerAdapter
import com.mambo.core.utils.LoadingDialog
import com.mambo.core.viewmodel.MainViewModel
import com.mambobryan.features.auth.databinding.FragmentAuthBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.getNavOptionsPopUpToCurrent
import com.mambobryan.navigation.extensions.navigate
import com.tapadoo.alerter.Alerter
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {

    private val binding by viewBinding(FragmentAuthBinding::bind)
    private val viewModel: AuthViewModel by viewModels()
    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViewPager()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    AuthEvent.NavigateToFeeds -> navigateToFeeds()
                    AuthEvent.NavigateToSetup -> navigateToSetup()
                    AuthEvent.NavigateToSignIn -> setViewPagerPosition(0)
                    AuthEvent.NavigateToSignUp -> setViewPagerPosition(1)
                    AuthEvent.HideLoadingDialog -> LoadingDialog.dismiss()
                    AuthEvent.ShowLoadingDialog -> LoadingDialog.show(requireContext())
                    is AuthEvent.ShowErrorMessage -> showError(event.message)
                    is AuthEvent.ShowSuccessMessage -> showSuccess(event.message)
                    AuthEvent.SetupDailyNotificationReminder -> sharedViewModel.setupDailyInteractionReminder()
                }
            }
        }

    }

    private fun showError(message: String) {
        val alert = AlertView("Error", "\n$message\n", AlertStyle.DIALOG)
        alert.addAction(AlertAction("dismiss", AlertActionStyle.DEFAULT) {})
        alert.show(requireActivity() as AppCompatActivity)
    }

    private fun showSuccess(message: String) {
        Alerter.create(requireActivity())
            .setText(message)
            .setIcon(R.drawable.ic_baseline_check_circle_24)
            .setBackgroundColorRes(R.color.success)
            .show()
    }

    private fun setViewPagerPosition(position: Int) {
        binding.vpAuth.setCurrentItem(position, true)
    }

    private fun setUpViewPager() {

        val fragments = arrayListOf(SignInFragment(), SignUpFragment())
        val adapter = ViewPagerAdapter(fragments, childFragmentManager, lifecycle)

        binding.apply {
            vpAuth.isUserInputEnabled = false
            vpAuth.adapter = adapter
        }

    }

    private fun navigateToFeeds() {
        val intent = requireActivity().intent

        requireActivity().finishAffinity().also {
            startActivity(intent)
        }
    }

    private fun navigateToSetup() {
        val deeplink = getDeeplink(Destinations.SETUP)
        navigate(deeplink, getNavOptionsPopUpToCurrent())
    }

}