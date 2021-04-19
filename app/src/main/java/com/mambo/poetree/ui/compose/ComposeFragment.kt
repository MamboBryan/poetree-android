package com.mambo.poetree.ui.compose

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentChoiceParentBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ComposeFragment : Fragment(R.layout.fragment_choice_parent) {

    private val binding by viewBinding(FragmentChoiceParentBinding::bind)
    private val viewModel by viewModels<ComposeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            tvComposeTitle.text = viewModel.poem?.title ?: "Untitled"

            btnChoiceNext.setOnClickListener {
                viewModel.onNextClicked(getViewPagerCurrentPosition())
            }

            btnChoiceBack.setOnClickListener {
                viewModel.onPreviousClicked(getViewPagerCurrentPosition())
            }

            setUpViewPager()

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.composePoemEvent.collect { event ->
                    when (event) {
                        is ComposeViewModel.ComposePoemEvent.NavigateBack -> {

                            if (event.message.isEmpty()) {
                                val position = binding.viewPagerChoice.currentItem - 1
                                setViewPagerItem(position)
                            } else {
                                findNavController().popBackStack()
                            }

                        }

                        is ComposeViewModel.ComposePoemEvent.NavigateNext -> {

                            val position = binding.viewPagerChoice.currentItem + 1
                            setViewPagerItem(position)

                        }

                        is ComposeViewModel.ComposePoemEvent.NavigateToPoem -> {
                            val action =
                                ComposeFragmentDirections.actionComposeFragmentToPoemFragment(event.poem)
                            findNavController().navigate(action)
                        }

                        is ComposeViewModel.ComposePoemEvent.NavigateToMyLibrary -> {
                            setFragmentResult(
                                "create_update_request",
                                bundleOf("create_update_result" to event.result)
                            )

                            findNavController().navigate(
                                ComposeFragmentDirections.actionComposeFragmentToHomeFragment2()
                            )
                        }

                        is ComposeViewModel.ComposePoemEvent.ShowIncompletePoemMessage -> {
                            Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT)
                                .show()
                        }
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
                                textView3.text = "Choose Topic to finish"

                                btnChoiceNext.text = "Save"
                                btnChoiceNext.isEnabled = true

                                btnChoiceBack.text = "Back"
                            }

                            else -> {
                                textView3.text = "Choose Emotion to continue"

                                btnChoiceNext.text = "Next"
                                btnChoiceNext.isEnabled = viewModel.emotion != null

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

            TabLayoutMediator(tabLayoutChoice, viewPagerChoice) { tab, position ->

            }.attach()

        }

    }

    private fun getViewPagerCurrentPosition(): Int {
        binding.apply {
            return viewPagerChoice.currentItem
        }
    }

    private fun setViewPagerItem(position: Int) {
        binding.apply {
            viewPagerChoice.setCurrentItem(position, true)
        }
    }

}