package com.mambo.bookmarks

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import com.mambo.bookmarks.databinding.FragmentBookmarksBinding
import com.mambo.bookmarks.databinding.ItemPoemBookmarkBinding
import com.mambo.core.adapters.GenericStateAdapter
import com.mambo.core.adapters.LazyPagingAdapter
import com.mambo.core.adapters.getInflater
import com.mambo.core.extensions.onQueryTextChanged
import com.mambo.core.utils.*
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.data.models.Poem
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.ocpsoft.prettytime.PrettyTime
import javax.inject.Inject

@AndroidEntryPoint
class BookmarksFragment : Fragment(R.layout.fragment_bookmarks) {

    private val sharedViewModel: MainViewModel by activityViewModels()
    private val viewModel: BookmarksViewModel by viewModels()

    private val binding by viewBinding(FragmentBookmarksBinding::bind)

    @Inject
    lateinit var bookmarkActions: BookmarkActions

    private val adapter = LazyPagingAdapter<Poem, ItemPoemBookmarkBinding>(Poem.COMPARATOR)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_bookmarks, menu)

        val searchItem = menu.findItem(R.id.action_bookmarks_search)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged { sharedViewModel.updateBookmarkQuery(it) }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        initViews()

        lifecycleScope.launch {
            sharedViewModel.bookmarkedPoems.collectLatest { adapter.submitData(it) }
        }

        lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collectLatest { state: CombinedLoadStates ->
                binding.layoutStateBookmarks.apply {
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
            viewModel.message.collectLatest {
                if (it != null) Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun initViews() {

        adapter.apply {

            onCreate {
                ItemPoemBookmarkBinding.inflate(it.getInflater(), it, false)
            }

            onBind { poem ->
                binding.apply {

                    val duration = PrettyTime().formatDuration(poem.createdAt.toDate())
                    val message = " \u2022 $duration "

                    tvPoemDuration.text = message
                    tvPoemUser.text = poem.user?.name
                    tvPoemTitle.text = poem.title

                    val color = poem.topic?.color ?: "#F7DEE6"
                    layoutBookmarkBg.setBackgroundColor(Color.parseColor(color))

                }
            }

            onItemClicked {
                bookmarkActions.navigateToPoem(it)
            }

            onSwipedRight(view = binding.layoutStateBookmarks.recyclerView) {
//            viewModel.deleteBookmark(it)
                Snackbar.make(requireView(), it.title, Snackbar.LENGTH_SHORT).show()
            }

            onSwipedLeft(view = binding.layoutStateBookmarks.recyclerView) {
//            viewModel.deleteBookmark(it)
                Snackbar.make(requireView(), it.title, Snackbar.LENGTH_SHORT).show()

            }

            withLoadStateHeaderAndFooter(
                header = GenericStateAdapter(adapter::retry),
                footer = GenericStateAdapter(adapter::retry)
            )
        }

        binding.apply {
            toolbarBookmarks.title = "Bookmarks"
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbarBookmarks)

            layoutStateBookmarks.apply {
                tvEmpty.text = "No Bookmark Found"
                tvError.text = "Couldn't load Bookmarks!"

                recyclerView.adapter = adapter

                buttonRetry.setOnClickListener { adapter.retry() }
                stateContent.setOnRefreshListener { adapter.refresh() }
            }

        }
    }

}