package com.mambo.explore

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.mambo.explore.databinding.FragmentExploreBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.navigate
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ExploreFragment : Fragment(R.layout.fragment_explore) {

    private val binding by viewBinding(FragmentExploreBinding::bind)
    private val viewModel: ExploreViewModel by viewModels()

    private val adapter = TopicsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    ExploreViewModel.ExploreEvent.NavigateToProfile -> navigateToProfile()
                    ExploreViewModel.ExploreEvent.NavigateToSearch -> navigateToSearch()
                    is ExploreViewModel.ExploreEvent.NavigateToSearchByTopic -> navigateToSearch()
                }
            }
        }


        viewModel.topics.observe(viewLifecycleOwner) { it ->

            val topics =
                listOf(
                    "nature",
                    "love",
                    "beauty",
                    "self",
                    "desire",
                    "motivation",
                    "relationships",
                    "death",
                    "spiritual",
                )

            binding.layoutState.apply {
                stateLoading.isVisible = it == null
                stateError.isVisible = it == null
                stateEmpty.isVisible = it.isEmpty()
                stateContent.isVisible = it.isNotEmpty()
            }

            adapter.submitList(topics)

        }
    }

    private fun initViews() {
        binding.apply {

            ivUserImage.setOnClickListener { viewModel.onProfileImageClicked() }
            layoutSearch.setOnClickListener { viewModel.onSearchFieldClicked() }

            layoutState.tvEmpty.text = "Topics are empty"
            layoutState.tvError.text = "Couldn't load topics!"

            layoutState.recyclerView.adapter = adapter
            layoutState.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            adapter.setListener(object : TopicsAdapter.OnTopicClickListener{
                override fun onTopicClicked(topic: String) {
                    viewModel.onTopicClicked()
                }
            })
        }
    }

    private fun navigateToProfile(){
        val deeplink = getDeeplink(Destinations.PROFILE)
        navigate(deeplink)
    }

    private fun navigateToSearch(){
        val deeplink = getDeeplink(Destinations.SEARCH)
        navigate(deeplink)
    }

}