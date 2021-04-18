package com.mambo.poetree.ui.compose

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentChoiceParentBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.flow.collect

class ComposeFragment : Fragment(R.layout.fragment_choice_parent) {

    private val binding by viewBinding(FragmentChoiceParentBinding::bind)
    private val viewModel by viewModels<ComposeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            btnChoiceNext.setOnClickListener {
                val position = viewPagerChoice.currentItem + 1
                viewModel.onPreviousClicked(position)
            }

            btnChoiceBack.setOnClickListener {
                val position = viewPagerChoice.currentItem - 1
                viewModel.onNextClicked(position)
            }

            setUpViewPager()

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.composePoemEvent.collect { event ->
                    when (event) {
                        is ComposeViewModel.ComposePoemEvent.NavigateBack -> TODO()
                        is ComposeViewModel.ComposePoemEvent.NavigateNext -> TODO()
                        is ComposeViewModel.ComposePoemEvent.NavigateToPoem -> TODO()
                        ComposeViewModel.ComposePoemEvent.NavigateToHomeFragment -> TODO()
                    }
                }
            }

        }
    }

    private fun setUpViewPager() {

        val fragments = arrayListOf(ChoiceEmotionFragment(), ChoiceTopicFragment())
        val adapter = ViewPagerAdapter(fragments, childFragmentManager, lifecycle)

        binding.apply {
            viewPagerChoice.isUserInputEnabled = false
            viewPagerChoice.adapter = adapter

            tabLayoutChoice.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {

                    val position = tab.position

                    binding.apply {
                        when (position) {
                            1 -> {
                                textView3.text = "Choose Topic"

                                btnChoiceNext.text = "Finish"
                                btnChoiceNext.isEnabled = viewModel.poemTopic.isNotEmpty()

                                btnChoiceBack.text = "Back"
                            }

                            else -> {
                                textView3.text = "Choose Emotion"

                                btnChoiceNext.text = "Next"
                                btnChoiceNext.isEnabled = viewModel.poemEmotion.isNotEmpty()

                                btnChoiceBack.text = "Cancel"
                            }
                        }
                    }

                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    //do nothing
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                    //do nothing
                }
            })
        }

    }

    private fun setViewPagerItem(position: Int) {
        binding.apply {
            viewPagerChoice.setCurrentItem(position, true)
        }
    }

}