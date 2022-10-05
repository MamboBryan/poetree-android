package com.mambo.features.home

import android.graphics.Color
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
import com.mambo.core.adapters.GenericStateAdapter
import com.mambo.core.adapters.LazyPagingAdapter
import com.mambo.core.adapters.getInflater
import com.mambo.core.extensions.startDrawable
import com.mambo.core.utils.*
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.data.models.Poem
import com.mambo.features.home.databinding.FragmentFeedBinding
import com.mambo.ui.databinding.ItemPoemBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import org.ocpsoft.prettytime.PrettyTime
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment(R.layout.fragment_feed) {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: FeedViewModel by viewModels()

    private val binding by viewBinding(FragmentFeedBinding::bind)

    @Inject
    lateinit var feedActions: FeedActions

    private val adapter = LazyPagingAdapter<Poem, ItemPoemBinding>(Poem.COMPARATOR)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        lifecycleScope.launchWhenStarted {
            mainViewModel.feedPoems.collectLatest { adapter.submitData(it) }
        }

        lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collectLatest { state: CombinedLoadStates ->
                binding.layoutFeedState.apply {
                    when (state.source.refresh) {
                        is LoadState.Loading -> showLoading()
                        is LoadState.Error -> showError()
                        is LoadState.NotLoading -> {
                            if (state.append.endOfPaginationReached && adapter.itemCount < 1)
                                showEmpty()
                            else
                                showContent()

                        }
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

        adapter.apply {
            onCreate { ItemPoemBinding.inflate(it.getInflater(), it, false) }
            onBind { poem: Poem ->
                this.apply {

                    val date = poem.updatedAt.toDate()
                    val duration = PrettyTime().formatDuration(date)
                    val message = " \u2022 $duration "

                    tvPoemUsername.text = poem.user?.name
                    tvPoemTitle.text = poem.title
                    tvPoemDate.text = message

                    val likeIcon = when (poem.liked) {
                        true -> com.mambo.ui.R.drawable.liked
                        false -> com.mambo.ui.R.drawable.unliked
                    }
                    tvLikes.apply {
                        text = prettyCount(poem.likes)
                        startDrawable(likeIcon)
                    }

                    val commentIcon = when (poem.commented) {
                        true -> com.mambo.ui.R.drawable.commented
                        false -> com.mambo.ui.R.drawable.uncommented
                    }
                    tvComments.apply {
                        text = prettyCount(poem.comments)
                        startDrawable(commentIcon)
                    }

                    val bookmarkIcon = when (poem.bookmarked) {
                        true -> com.mambo.ui.R.drawable.bookmarked
                        false -> com.mambo.ui.R.drawable.unbookmarked
                    }
                    tvBookmarks.apply {
                        text = prettyCount(poem.bookmarks)
                        startDrawable(bookmarkIcon)
                    }

                    val readIcon = when (poem.read) {
                        true -> com.mambo.ui.R.drawable.read
                        false -> com.mambo.ui.R.drawable.unread
                    }
                    tvReads.apply {
                        text = prettyCount(poem.reads)
                        startDrawable(readIcon)
                    }

                    val color = poem.topic?.color ?: "#fefefe"
                    layoutPoem.setBackgroundColor(Color.parseColor(color))

                }
            }
            onItemClicked { navigateToPoem(poem = it) }
            withLoadStateHeaderAndFooter(
                header = GenericStateAdapter(adapter::retry),
                footer = GenericStateAdapter(adapter::retry)
            )
        }

        binding.apply {

            imageUser.setOnClickListener { navigateToProfile() }
            ivFeedSettings.setOnClickListener { navigateToSettings() }
            btnCreatePoem.setOnClickListener { navigateToCompose() }

            layoutFeedState.apply {
                buttonRetry.setOnClickListener { adapter.retry() }
                stateContent.setOnRefreshListener { adapter.refresh() }
                recyclerView.adapter = adapter
            }

        }

    }

    private fun navigateToProfile() {
        feedActions.navigateToProfile()
    }

    private fun navigateToCompose() {
        feedActions.navigateToCompose()
    }

    private fun navigateToPoem(poem: Poem) {
        feedActions.navigateToPoem(poem = poem)
    }

    private fun navigateToSettings() {
        feedActions.navigateToSettings()
    }

}