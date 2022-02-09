package com.mambobryan.features.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mambobryan.features.onboarding.databinding.FragmentOnboardingBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.getNavOptionsPopUpToCurrent
import com.mambobryan.navigation.extensions.navigate
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class OnBoardingFragment : Fragment(R.layout.fragment_onboarding) {

    private val binding by viewBinding(FragmentOnboardingBinding::bind)
    private val viewModel by viewModels<OnboardViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOnBoardingScreens()

    }

    private fun setupOnBoardingScreens() {

        val fragments = arrayListOf(ReaderFragment(), WriterFragment(), CommunityFragment())
        val adapter = ViewPager(childFragmentManager, fragments)

        binding.apply { pager.adapter = adapter }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.onBoardEvent.collect { event ->
                when (event) {
                    OnboardViewModel.OnboardEvent.NavigateToAuthentication -> navigateToAuth()
                }
            }
        }
    }

    private fun navigateToAuth() {
        val deeplink = getDeeplink(Destinations.LANDING)
        val optons = getNavOptionsPopUpToCurrent()
        navigate(deeplink, optons)
    }

    // creation of constructor of viewPager class
    inner class ViewPager(
        fm: FragmentManager,
        private val fragments: List<Fragment>,
        behaviour: Int = FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) : FragmentPagerAdapter(fm, behaviour) {

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }
    }

}