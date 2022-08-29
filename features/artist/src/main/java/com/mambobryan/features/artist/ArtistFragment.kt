package com.mambobryan.features.artist

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.mambo.core.OnPoemClickListener
import com.mambo.core.adapters.ArtistPoemsAdapter
import com.mambo.core.adapters.GenericStateAdapter
import com.mambo.core.utils.prettyCount
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.data.models.Poem
import com.mambobryan.features.artist.databinding.FragmentArtistBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.navigate
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class ArtistFragment : Fragment(R.layout.fragment_artist) {

    private val sharedViewModel: MainViewModel by activityViewModels()
    private val viewModel: ArtistViewModel by viewModels()

    private val binding by viewBinding(FragmentArtistBinding::bind)

    @Inject
    lateinit var adapter: ArtistPoemsAdapter

    private var titleMaxScrollHeight = 0f
    private var headerMaxHeight = 0f
    private var avatarTop = 0f
    private var maxScrollHeight = 0f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    ArtistViewModel.ArtistEvent.NavigateToPoem -> navigateToPoem()
                    is ArtistViewModel.ArtistEvent.UpdatePoemInSharedViewModel -> {
                        sharedViewModel.setPoem(event.poem)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.poems.collectLatest { adapter.submitData(it) }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadState ->
                binding.layoutArtistState.apply {

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

    private fun initViews() = binding.apply {
        layoutHeader.ivHeaderDarkMode.isVisible = false
        layoutHeader.ivHeaderBack.setOnClickListener { findNavController().popBackStack() }

        setupUserView()

        setUpScrollView()

        layoutArtistState.apply {
            buttonRetry.setOnClickListener { adapter.retry() }
            recyclerView.adapter = adapter
            recyclerView.setHasFixedSize(true)
            stateContent.setOnRefreshListener {
                recyclerView.scrollToPosition(0)
                adapter.refresh()
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

    private fun setupUserView() = binding.apply {
        val user = sharedViewModel.user.value
        layoutHeader.tvHeaderTitle.text = "MamboBryan"
        layoutDetails.apply {
            tvArtistName.text = "MamboBryan"
//            tvArtistBio.text =""
            tvArtistReads.text = prettyCount(2000000)
            tvArtistBookmarks.text = prettyCount(20000)
            tvArtistLikes.text = prettyCount(200000)
        }
    }

    private fun setUpScrollView() = binding.apply {
        layoutHeader.ivHeaderSplit.visibility = View.GONE
        layoutHeader.tvHeaderTitle.translationY = -1000f
        layoutScrollRoot.setOnScrollListener { ty, _ ->

            var translationY = ty
            translationY = -translationY

            val tvTitle = layoutHeader.tvHeaderTitle
            val tvName = layoutDetails.tvArtistName
            val ivArtistImage = layoutDetails.ivArtistMage
            val ivSplit = layoutHeader.ivHeaderSplit

            if (titleMaxScrollHeight == 0f) {
                titleMaxScrollHeight = ((tvTitle.parent as View).bottom - tvTitle.top).toFloat()
                maxScrollHeight = headerMaxHeight + titleMaxScrollHeight
            }

            if (headerMaxHeight == 0f) {
                headerMaxHeight = tvName.top.toFloat()
                maxScrollHeight = headerMaxHeight + titleMaxScrollHeight
            }

            if (avatarTop == 0f) {
                avatarTop = ivArtistImage.top.toFloat()
            }

            var alpha = 0
            val baseAlpha = 60
            if (0 > avatarTop + translationY) {
                alpha =
                    255.coerceAtMost((abs(avatarTop + translationY) * (255 - baseAlpha) / (headerMaxHeight - avatarTop) + baseAlpha).toInt())
                ivSplit.visibility = View.VISIBLE
            } else {
                ivSplit.visibility = View.GONE
            }
            ivSplit.background.alpha = alpha
            tvTitle.translationY =
                (0.coerceAtLeast(maxScrollHeight.toInt() + translationY)).toFloat()
        }
    }

    private fun navigateToPoem() {
        val deeplink = getDeeplink(Destinations.POEM)
        navigate(deeplink)
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.setUser(null)
    }
}