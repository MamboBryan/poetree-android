package com.mambo.features.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import coil.load
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.mambo.core.OnPoemClickListener
import com.mambo.core.adapters.GenericStateAdapter
import com.mambo.core.adapters.PoemPagingAdapter
import com.mambo.core.utils.showContent
import com.mambo.core.utils.showEmpty
import com.mambo.core.utils.showError
import com.mambo.core.utils.showLoading
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.data.models.Poem
import com.mambo.features.home.databinding.FragmentFeedBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.navigate
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment(R.layout.fragment_feed) {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: FeedViewModel by viewModels()

    private val binding by viewBinding(FragmentFeedBinding::bind)

    @Inject
    lateinit var adapter: PoemPagingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        lifecycleScope.launchWhenStarted {
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

        lifecycleScope.launchWhenStarted {
            mainViewModel.feedPoems.collectLatest { adapter.submitData(it) }
        }

        lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collectLatest { state: CombinedLoadStates ->
                when (state.source.refresh) {
                    is LoadState.Loading -> binding.layoutFeedState.showLoading()
                    is LoadState.Error -> binding.layoutFeedState.showError()
                    is LoadState.NotLoading -> {
                        if (state.append.endOfPaginationReached && adapter.itemCount < 1)
                            binding.layoutFeedState.showEmpty()
                        else
                            binding.layoutFeedState.showContent()

                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.imageUrl.collectLatest {
                binding.imageUser.load(it) {
                    scale(Scale.FILL)
                    crossfade(true)
                    placeholder(R.drawable.ic_baseline_account_circle_24)
                    error(R.drawable.ic_baseline_account_circle_24)
                    transformations(CircleCropTransformation())
                }
            }
        }

    }

    private fun initViews() {
        binding.apply {

            imageUser.setOnClickListener { viewModel.onUserImageClicked() }
            ivFeedSettings.setOnClickListener { viewModel.onSettingsClicked() }
            btnCreatePoem.setOnClickListener { viewModel.onCreatePoemClicked() }

            layoutFeedState.apply {
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