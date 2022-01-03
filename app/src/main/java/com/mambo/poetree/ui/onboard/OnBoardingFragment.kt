package com.mambo.poetree.ui.onboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentOnboardingBinding
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

        binding.apply {
            pager.adapter = adapter
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.onBoardEvent.collect { event ->
                when (event) {
                    OnboardViewModel.OnboardEvent.NavigateToAuthentication -> finishOnBoarding()
                }
            }
        }
    }

    private fun finishOnBoarding() {

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