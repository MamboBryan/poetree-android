package com.mambobryan.features.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mambo.core.adapters.ViewPagerAdapter
import com.mambo.core.utils.LoadingDialog
import com.mambobryan.features.auth.databinding.FragmentAuthBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.getNavOptionsPopUpToCurrent
import com.mambobryan.navigation.extensions.navigate
import com.tapadoo.alerter.Alerter
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {

    private val binding by viewBinding(FragmentAuthBinding::bind)
    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViewPager()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    AuthEvent.NavigateToFeeds -> navigateToFeeds()
                    AuthEvent.NavigateToSetup -> navigateToSetup()
                    AuthEvent.NavigateToSignIn -> binding.vpAuth.setCurrentItem(0, true)
                    AuthEvent.NavigateToSignUp -> binding.vpAuth.setCurrentItem(1, true)
                    AuthEvent.HideLoadingDialog -> LoadingDialog.dismiss()
                    AuthEvent.ShowLoadingDialog -> LoadingDialog.show(requireContext())
                    is AuthEvent.ShowErrorMessage -> {
                        Alerter.create(requireActivity())
                            .setText(event.message)
                            .setIcon(R.drawable.ic_baseline_check_circle_24)
                            .setBackgroundColorRes(R.color.success)
                            .show()
                    }
                    is AuthEvent.ShowSuccessMessage -> {
                        Alerter.create(requireActivity())
                            .setText(event.message)
                            .setIcon(R.drawable.ic_baseline_error_24)
                            .setBackgroundColorRes(R.color.error)
                            .show()
                    }
                }
            }
        }

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
        val deeplink = getDeeplink(Destinations.FEED)
        navigate(deeplink, getNavOptionsPopUpToCurrent())
    }

    private fun navigateToSetup() {
        val deeplink = getDeeplink(Destinations.SETUP)
        navigate(deeplink, getNavOptionsPopUpToCurrent())
    }

}