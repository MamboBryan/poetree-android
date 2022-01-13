package com.mambo.features.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.mambo.core.adapters.PoemPagingAdapter
import com.mambo.core.adapters.PoemStateAdapter
import com.mambo.core.viewmodel.MainViewModel
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

        lifecycleScope.launch {
            Log.i("FEEDS", "launching")

            viewModel.events.collect { event ->
                when (event) {
                    FeedViewModel.FeedEvent.NavigateToProfile -> navigateToProfile()
                    FeedViewModel.FeedEvent.NavigateToCompose -> navigateToCompose()
                    is FeedViewModel.FeedEvent.NavigateToPoem -> navigateToPoem()
                }
            }
            viewModel.feeds.collectLatest {
                Log.i("FEEDS", "data collected")
                adapter.submitData(it)
            }
            adapter.loadStateFlow.collectLatest { loadState ->
                Log.i("FEEDS", "load states collected")
                binding.layoutState.apply {

                    stateContent.isVisible = false
                    stateEmpty.isVisible = false
                    stateError.isVisible = false
                    stateLoading.isVisible = false

                    when (loadState.source.refresh) {
                        is LoadState.Loading -> {
                            stateLoading.isVisible = true
                            Log.i("FEEDS", "gettingFeeds: LOADING")
                        }

                        is LoadState.Error -> {
                            stateError.isVisible = true
                            stateContent.isRefreshing = false
                            Log.i("FEEDS", "gettingFeeds: ERROR")
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
                            Log.i("FEEDS", "gettingFeeds: NOT LOADING")

                        }
                    }
                }
            }

        }
    }

    private fun initViews() {
        binding.apply {
            imageUser.setOnClickListener { viewModel.onUserImageClicked() }
            btnCreatePoem.setOnClickListener { viewModel.onCreatePoemClicked() }
            layoutState.buttonRetry.setOnClickListener { adapter.retry() }

            layoutState.recyclerView.adapter = adapter
            layoutState.recyclerView.setHasFixedSize(true)
            layoutState.stateContent.setOnRefreshListener {
                layoutState.recyclerView.scrollToPosition(0)
                adapter.refresh()
            }

        }

        adapter.withLoadStateHeaderAndFooter(
            header = PoemStateAdapter(adapter::retry),
            footer = PoemStateAdapter(adapter::retry)
        )

//        adapter.addLoadStateListener { loadState ->
//
//        }
    }

    private fun navigateToProfile() {
        val deeplink = getDeeplink(Destinations.PROFILE)
        navigate(deeplink)
    }

    private fun navigateToCompose() {
        val deeplink = getDeeplink(Destinations.COMPOSE)
        navigate(deeplink)
    }

    private fun navigateToPoem(){
        val deeplink = getDeeplink(Destinations.POEM)
        navigate(deeplink)
    }


}