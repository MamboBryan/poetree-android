package com.mambobryan.features.profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.irozon.alertview.AlertActionStyle
import com.irozon.alertview.AlertStyle
import com.irozon.alertview.AlertTheme
import com.irozon.alertview.AlertView
import com.irozon.alertview.objects.AlertAction
import com.mambo.core.OnPoemClickListener
import com.mambo.core.adapters.ArtistPoemsAdapter
import com.mambo.core.adapters.GenericStateAdapter
import com.mambo.core.utils.prettyCount
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.data.models.Poem
import com.mambobryan.features.profile.databinding.FragmentProfileBinding
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
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val binding by viewBinding(FragmentProfileBinding::bind)

    private val viewModel: ProfileViewModel by viewModels()
    private val sharedViewModel: MainViewModel by activityViewModels()

    @Inject
    lateinit var adapter: ArtistPoemsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    ProfileViewModel.ProfileEvent.ShowDarkModeDialog -> showDarkModeDialog()
                    is ProfileViewModel.ProfileEvent.NavigateToPoem -> {
                        sharedViewModel.setPoem(event.poem)
                        navigateToLanding()
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            sharedViewModel.myPublicPoems.collectLatest { adapter.submitData(it) }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadState ->
                binding.layoutProfileState.apply {

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

        setupUserView()
        setUpScrollView()

        layoutProfileHeader.ivHeaderDarkMode.setOnClickListener { viewModel.onAppThemeClicked() }
        layoutProfileHeader.ivHeaderBack.setOnClickListener { findNavController().popBackStack() }

        layoutProfileState.apply {
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
//        val user = sharedViewModel.user.value
        layoutProfileHeader.tvHeaderTitle.text = "MamboBryan"
        layoutProfileDetails.apply {
            tvArtistName.text = "MamboBryan"
//            tvArtistBio.text =""
            tvArtistReads.text = prettyCount(2000000)
            tvArtistBookmarks.text = prettyCount(20000)
            tvArtistLikes.text = prettyCount(200000)
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

    private fun navigateToLanding() {
        navigate(getDeeplink(Destinations.POEM))
    }

}