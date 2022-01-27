package com.mambo.explore

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.mambo.core.OnTopicClickListener
import com.mambo.core.adapters.GenericStateAdapter
import com.mambo.core.adapters.TopicPagingAdapter
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.data.models.Topic
import com.mambo.explore.databinding.FragmentExploreBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.navigate
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExploreFragment : Fragment(R.layout.fragment_explore) {

    private val binding by viewBinding(FragmentExploreBinding::bind)
    private val viewModel: ExploreViewModel by viewModels()
    private val sharedViewModel: MainViewModel by activityViewModels()

    private val adapter = TopicPagingAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        lifecycleScope.launch {
            viewModel.events.collect { event ->
                when (event) {
                    ExploreViewModel.ExploreEvent.NavigateToProfile -> navigateToProfile()
                    ExploreViewModel.ExploreEvent.NavigateToSearch -> navigateToSearch()
                    is ExploreViewModel.ExploreEvent.NavigateToSearchByTopic -> navigateToSearch()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.topics.collectLatest {
                adapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadState ->
                binding.layoutStateExplore.apply {

                    stateContent.isVisible = false
                    stateEmpty.isVisible = false
                    stateError.isVisible = false
                    stateLoading.isVisible = false

                    when (loadState.source.refresh) {
                        is LoadState.Loading -> {
                            stateLoading.isVisible = true
                        }

                        is LoadState.Error -> {
                            stateError.isVisible = true
                        }

                        is LoadState.NotLoading -> {

                            if (loadState.append.endOfPaginationReached) {
                                if (adapter.itemCount < 1)
                                    stateEmpty.isVisible = true
                                else {
                                    stateContent.isVisible = true
                                }
                            } else {
                                stateContent.isVisible = true
                            }


                        }
                    }
                }
            }
        }

    }

    private fun initViews() {
        binding.apply {

            ivUserImage.setOnClickListener { viewModel.onProfileImageClicked() }
            layoutSearch.setOnClickListener { viewModel.onSearchFieldClicked() }

            layoutStateExplore.apply {
                tvEmpty.text = "Topics are empty"
                tvError.text = "Couldn't load topics!"

                recyclerView.adapter = adapter
                recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
                buttonRetry.setOnClickListener { adapter.retry() }

            }
        }
        adapter.setListener(object : OnTopicClickListener {
            override fun onTopicClicked(topic: Topic) {
                sharedViewModel.setTopic(topic)
                viewModel.onTopicClicked()
            }
        })
        adapter.withLoadStateHeaderAndFooter(
            header = GenericStateAdapter(adapter::retry),
            footer = GenericStateAdapter(adapter::retry)
        )
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