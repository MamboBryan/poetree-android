package com.mambobryan.features.profile

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
import com.irozon.alertview.AlertActionStyle
import com.irozon.alertview.AlertStyle
import com.irozon.alertview.AlertTheme
import com.irozon.alertview.AlertView
import com.irozon.alertview.objects.AlertAction
import com.mambo.core.adapters.GenericStateAdapter
import com.mambo.core.adapters.LazyPagingAdapter
import com.mambo.core.adapters.getInflater
import com.mambo.core.utils.*
import com.mambo.data.models.Poem
import com.mambo.ui.databinding.ItemPoemArtistBinding
import com.mambobryan.features.profile.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import org.ocpsoft.prettytime.PrettyTime
import java.util.*
import javax.inject.Inject
import kotlin.math.abs


@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    @Inject
    lateinit var profileActions: ProfileActions

    private val binding by viewBinding(FragmentProfileBinding::bind)
    private val viewModel: ProfileViewModel by viewModels()
    private val adapter = LazyPagingAdapter<Poem, ItemPoemArtistBinding>(Poem.COMPARATOR)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    ProfileViewModel.ProfileEvent.ShowDarkModeDialog -> showDarkModeDialog()
                    ProfileViewModel.ProfileEvent.HideLoading -> {
                        binding.layoutProfileDetails.progressProfile.isVisible = false
                    }
                    is ProfileViewModel.ProfileEvent.ShowError -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collectLatest { state: CombinedLoadStates ->
                when (state.source.refresh) {
                    is LoadState.Loading -> binding.layoutProfilePoems.showLoading()
                    is LoadState.Error -> binding.layoutProfilePoems.showError()
                    is LoadState.NotLoading -> {
                        if (state.append.endOfPaginationReached && adapter.itemCount < 1)
                            binding.layoutProfilePoems.showEmpty()
                        else
                            binding.layoutProfilePoems.showContent()

                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.userDetails.collectLatest {
                it?.let { userDetails ->
                    binding.layoutProfileHeader.tvHeaderTitle.text = userDetails.name
                    binding.layoutProfileDetails.apply {
                        progressProfile.isVisible = false
                        tvArtistName.text = userDetails.name
                        tvArtistBio.text = userDetails.bio
                        tvArtistReads.text = prettyCount(userDetails.reads ?: 0)
                        tvArtistBookmarks.text = prettyCount(userDetails.bookmarks ?: 0)
                        tvArtistLikes.text = prettyCount(userDetails.likes ?: 0)

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

        lifecycleScope.launchWhenResumed {
            viewModel.poems.collectLatest {
                adapter.submitData(it)
                binding.layoutProfilePoems.stateContent.isRefreshing = false
            }
        }

    }

    private fun initViews() {
        adapter.apply {
            onCreate { ItemPoemArtistBinding.inflate(it.getInflater(), it, false) }
            onItemClicked { profileActions.navigateToPoem(it) }
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

            setUpScrollView()

            layoutProfileHeader.ivHeaderDarkMode.setOnClickListener { viewModel.onAppThemeClicked() }
            layoutProfileHeader.ivHeaderBack.setOnClickListener { findNavController().popBackStack() }

            layoutProfilePoems.apply {
                buttonRetry.setOnClickListener { adapter.retry() }
                recyclerView.adapter = adapter
                stateContent.setOnRefreshListener {
                    adapter.refresh()
                }
            }

        }
    }

    private fun setUpScrollView() = binding.apply {
        layoutProfileHeader.ivHeaderSplit.visibility = View.GONE
        layoutProfileHeader.tvHeaderTitle.translationY = -1000f
        layoutScrollRoot.setOnScrollListener { ty, _ ->

            var titleMaxScrollHeight = 0f
            var headerMaxHeight = 0f
            var avatarTop = 0f
            var maxScrollHeight = 0f

            var translationY = ty
            translationY = -translationY

            val tvTitle = layoutProfileHeader.tvHeaderTitle
            val tvName = layoutProfileDetails.tvArtistName
            val ivArtistImage = layoutProfileDetails.ivArtistMage
            val ivSplit = layoutProfileHeader.ivHeaderSplit

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

    private fun showDarkModeDialog() {

        val modes = listOf(
            AppCompatDelegate.MODE_NIGHT_NO,
            AppCompatDelegate.MODE_NIGHT_YES,
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )

        val isDark = viewModel.mode == modes[1] || viewModel.mode == modes[2]

        val alert = AlertView("Choose Theme", "", AlertStyle.BOTTOM_SHEET)
        alert.addAction(
            AlertAction("Light", AlertActionStyle.DEFAULT) {
                viewModel.onDarkModeSelected(modes[0])
            }
        )
        alert.addAction(
            AlertAction("Dark", AlertActionStyle.DEFAULT) {
                viewModel.onDarkModeSelected(modes[1])
            }
        )
        alert.addAction(
            AlertAction("System Default", AlertActionStyle.DEFAULT) {
                viewModel.onDarkModeSelected(modes[2])
            }
        )
        alert.setTheme(if (isDark) AlertTheme.DARK else AlertTheme.LIGHT)
        alert.show(requireActivity() as AppCompatActivity)

    }

}