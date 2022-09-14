package com.mambobryan.features.artist

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import coil.load
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.google.android.material.snackbar.Snackbar
import com.mambo.core.adapters.GenericStateAdapter
import com.mambo.core.adapters.LazyPagingAdapter
import com.mambo.core.adapters.getInflater
import com.mambo.core.utils.*
import com.mambo.data.models.Poem
import com.mambo.ui.databinding.ItemPoemArtistBinding
import com.mambobryan.features.artist.databinding.FragmentArtistBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import org.ocpsoft.prettytime.PrettyTime
import java.util.*
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class ArtistFragment : Fragment(R.layout.fragment_artist) {

    @Inject
    lateinit var artistActions: ArtistActions

    private val viewModel: ArtistViewModel by viewModels()
    private val binding by viewBinding(FragmentArtistBinding::bind)
    private val adapter = LazyPagingAdapter<Poem, ItemPoemArtistBinding>(Poem.COMPARATOR)
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
                    ArtistViewModel.ArtistEvent.HideLoading -> {
                        binding.layoutDetails.progressProfile.isVisible = false
                    }
                    is ArtistViewModel.ArtistEvent.ShowError -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.poems.collectLatest {
                if (it != null) adapter.submitData(it)
                binding.layoutArtistState.stateContent.isRefreshing = false
            }
        }

        lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collectLatest { state: CombinedLoadStates ->
                binding.layoutArtistState.apply {
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
            viewModel.user.collectLatest {
                it?.let {
                    binding.apply {
                        layoutHeader.tvHeaderTitle.text = it.name
                        layoutDetails.apply {
                            tvArtistName.text = it.name
                            tvArtistBio.text = it.bio
                            tvArtistReads.text = prettyCount(0)
                            tvArtistBookmarks.text = prettyCount(0)
                            tvArtistLikes.text = prettyCount(0)

                            ivArtistMage.load(it.image) {
                                scale(Scale.FILL)
                                crossfade(true)
                                placeholder(R.drawable.ic_baseline_account_circle_24)
                                error(R.drawable.ic_baseline_account_circle_24)
                                transformations(CircleCropTransformation())
                            }
                        }

                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.details.collectLatest {
                it?.let {
                    binding.apply {
                        layoutHeader.tvHeaderTitle.text = it.name
                        layoutDetails.apply {
                            progressProfile.isVisible = false
                            tvArtistName.text = it.name
                            tvArtistBio.text = it.bio
                            tvArtistReads.text = prettyCount(it.reads ?: 0)
                            tvArtistBookmarks.text = prettyCount(it.bookmarks ?: 0)
                            tvArtistLikes.text = prettyCount(it.likes ?: 0)
                        }
                    }
                }
            }
        }

    }

    private fun initViews() {

        adapter.apply {

            onCreate { ItemPoemArtistBinding.inflate(it.getInflater(), it, false) }
            onItemClicked { artistActions.navigateToPoem(it) }
            onBind { poem ->
                this.apply {

                    val topic = poem.topic?.name?.replaceFirstChar { it.uppercase() } ?: "Topicless"

                    val date = Date(poem.createdAt.toDateTime() ?: 0)
                    val duration = PrettyTime().formatDuration(date)
                    val message = "$topic  \u2022  $duration "

                    tvPoemTitle.text = poem.title
                    tvPoemDuration.text = message

                    val color = poem.topic?.color ?: "#94F292"
                    layoutArtistBg.setBackgroundColor(Color.parseColor(color))

                }
            }
            withLoadStateHeaderAndFooter(
                header = GenericStateAdapter(adapter::retry),
                footer = GenericStateAdapter(adapter::retry)
            )
        }

        binding.apply {

            layoutHeader.ivHeaderDarkMode.isVisible = false
            layoutHeader.ivHeaderBack.setOnClickListener { findNavController().popBackStack() }

            setUpScrollView()

            layoutArtistState.apply {
                buttonRetry.setOnClickListener { adapter.retry() }
                recyclerView.adapter = adapter
                recyclerView.setHasFixedSize(true)
                stateContent.setOnRefreshListener {
                    adapter.refresh()
                }
            }

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

}