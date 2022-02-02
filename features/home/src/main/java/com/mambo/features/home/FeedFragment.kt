package com.mambo.features.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.mambo.core.OnPoemClickListener
import com.mambo.core.adapters.GenericStateAdapter
import com.mambo.core.adapters.PoemPagingAdapter
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.data.models.Poem
import com.mambo.features.home.databinding.FragmentFeedBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.navigate
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedFragment : Fragment(R.layout.fragment_feed) {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: FeedViewModel by viewModels()

    private val binding by viewBinding(FragmentFeedBinding::bind)
    private val adapter by lazy { PoemPagingAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        lifecycleScope.launchWhenCreated {
            viewModel.events.collect { event ->
                when (event) {
                    FeedViewModel.FeedEvent.NavigateToProfile -> navigateToProfile()
                    FeedViewModel.FeedEvent.NavigateToCompose -> navigateToCompose()
                    FeedViewModel.FeedEvent.NavigateToSettings -> navigateToSettings()
                    is FeedViewModel.FeedEvent.NavigateToPoem -> {
                        mainViewModel.setPoem(event.poem)
                        navigateToPoem()
                    }
                }
            }
        }

        lifecycleScope.launch {
            mainViewModel.feeds.collectLatest { adapter.submitData(it) }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadState ->
                binding.layoutState.apply {

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
                            stateContent.isRefreshing = false
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

                            stateContent.isRefreshing = false

                        }
                    }
                }
            }
        }

    }

    private fun initViews() {
        binding.apply {

            imageUser.setOnClickListener { viewModel.onUserImageClicked() }
            ivFeedSettings.setOnClickListener { viewModel.onSettingsClicked() }
            btnCreatePoem.setOnClickListener { viewModel.onCreatePoemClicked() }

            layoutState.apply {
                buttonRetry.setOnClickListener { adapter.retry() }
                recyclerView.adapter = adapter
                recyclerView.setHasFixedSize(true)
                stateContent.setOnRefreshListener {
                    recyclerView.scrollToPosition(0)
                    adapter.refresh()
                }
            }

        }

        adapter.setListener(object : OnPoemClickListener {
            override fun onPoemClicked(poem: Poem) {
                viewModel.onPoemClicked(poem)
            }

        })

        adapter.withLoadStateHeaderAndFooter(
            header = GenericStateAdapter(adapter::retry),
            footer = GenericStateAdapter(adapter::retry)
        )

    }

    private fun navigateToProfile() {
        val deeplink = getDeeplink(Destinations.PROFILE)
        navigate(deeplink)
    }

    private fun navigateToCompose() {
        val deeplink = getDeeplink(Destinations.COMPOSE)
        navigate(deeplink)
    }

    private fun navigateToPoem() {
        val deeplink = getDeeplink(Destinations.POEM)
        navigate(deeplink)
    }

    private fun navigateToSettings() {
        navigate(getDeeplink(Destinations.SETTINGS))
    }

}