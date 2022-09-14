package com.mambobryan.features.search

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.mambo.core.adapters.GenericStateAdapter
import com.mambo.core.adapters.LazyPagingAdapter
import com.mambo.core.adapters.getInflater
import com.mambo.core.extensions.startDrawable
import com.mambo.core.utils.*
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.data.models.Poem
import com.mambo.ui.databinding.ItemPoemBinding
import com.mambobryan.features.search.databinding.FragmentSearchBinding
import com.mambobryan.libraries.searchbar.model.MultiSearchChangeListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import org.ocpsoft.prettytime.PrettyTime
import javax.inject.Inject


@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    @Inject
    lateinit var searchActions: SearchActions

    private val sharedViewModel: MainViewModel by activityViewModels()
    private val viewModel: SearchViewModel by viewModels()

    private val binding by viewBinding(FragmentSearchBinding::bind)

    private val adapter = LazyPagingAdapter<Poem, ItemPoemBinding>(Poem.COMPARATOR)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setupSearchView()

        viewModel.topic.observe(viewLifecycleOwner) {
            sharedViewModel.setTopic(it)
        }

        lifecycleScope.launchWhenStarted {
            sharedViewModel.searchedPoems.collectLatest {
                adapter.submitData(it)
                binding.layoutPoems.stateContent.isRefreshing = false
            }
        }

        lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collectLatest { state: CombinedLoadStates ->
                binding.layoutPoems.apply {
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

    }

    private fun setupSearchView() = binding.apply {

        searchViewSearch.setSearchViewListener(object : MultiSearchChangeListener {
            override fun onTextChanged(index: Int, charSequence: CharSequence) {
                //do nothing
            }

            override fun onSearchComplete(index: Int, charSequence: CharSequence) {
                sharedViewModel.onSearchQueryUpdated(charSequence.toString())
                hideKeyboard()
            }

            override fun onSearchItemRemoved(index: Int) {
                hideKeyboard()
            }

            override fun onItemSelected(index: Int, charSequence: CharSequence) {
                sharedViewModel.onSearchQueryUpdated(charSequence.toString())
            }
        })

    }

    private fun initViews() {

        adapter.apply {
            onCreate { ItemPoemBinding.inflate(it.getInflater(), it, false) }
            onItemClicked { poem -> searchActions.navigateToPoem(poem) }
            onBind { poem ->
                binding.apply {

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
            withLoadStateHeaderAndFooter(
                header = GenericStateAdapter(adapter::retry),
                footer = GenericStateAdapter(adapter::retry)
            )
        }

        binding.apply {

            NavigationUI.setupWithNavController(toolbarSearch, findNavController())
            toolbarSearch.title = null

            layoutPoems.apply {
                tvEmpty.text = "Empty"
                tvError.text = "Error Loading Poems"
                stateContent.setOnRefreshListener { adapter.refresh() }
                recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        fabSearch.apply {
                            when {
                                dy > 0 && !isShown -> show()
                                dy < 0 && isShown -> hide()
                            }
                        }
                    }
                })
                recyclerView.adapter = adapter
            }
        }

    }

}