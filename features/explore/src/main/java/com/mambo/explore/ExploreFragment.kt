package com.mambo.explore

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.mambo.core.adapters.GenericStateAdapter
import com.mambo.core.adapters.TopicPagingAdapter
import com.mambo.core.utils.showContent
import com.mambo.core.utils.showEmpty
import com.mambo.core.utils.showError
import com.mambo.core.utils.showLoading
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.explore.databinding.FragmentExploreBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.navigate
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
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
                    ExploreViewModel.ExploreEvent.NavigateToSearch -> navigateToSearch()
                    is ExploreViewModel.ExploreEvent.NavigateToSearchByTopic -> navigateToSearch()
                }
            }
        }

        lifecycleScope.launch {
            sharedViewModel.topics.collectLatest {
                adapter.submitData(it)
            }
        }

        lifecycleScope.launchWhenResumed {
            adapter.loadStateFlow.collectLatest { state: CombinedLoadStates ->
                when (state.mediator?.refresh) {
                    is LoadState.Loading -> binding.layoutStateExplore.showLoading()
                    is LoadState.Error -> binding.layoutStateExplore.showError()
                    is LoadState.NotLoading -> {
                        if (state.append.endOfPaginationReached && adapter.itemCount < 1)
                            binding.layoutStateExplore.showEmpty()
                        else
                            binding.layoutStateExplore.showContent()

                    }
                    null -> binding.layoutStateExplore.showLoading()
                }
            }
        }

    }

    private fun initViews() {
        binding.apply {

            layoutSearch.setOnClickListener { viewModel.onSearchFieldClicked() }

            layoutStateExplore.apply {
                tvEmpty.text = "Topics are empty"
                tvError.text = "Couldn't load topics!"

                recyclerView.adapter = adapter
                recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
                buttonRetry.setOnClickListener { adapter.retry() }
            }

            fabTopic.setOnClickListener {
                navigateToTopic()
            }

        }

        adapter.setOnTopicClicked {
            sharedViewModel.setTopic(it)
            viewModel.onTopicClicked()
        }

        adapter.setOnUpdateClicked {
            sharedViewModel.setTopic(it)
            navigateToTopic()
        }

        adapter.withLoadStateHeaderAndFooter(
            header = GenericStateAdapter(adapter::retry),
            footer = GenericStateAdapter(adapter::retry)
        )
    }

    private fun navigateToSearch() {
        val deeplink = getDeeplink(Destinations.SEARCH)
        navigate(deeplink)
    }

    private fun navigateToTopic() {
        val deeplink = getDeeplink(Destinations.TOPIC)
        navigate(deeplink)
    }

}