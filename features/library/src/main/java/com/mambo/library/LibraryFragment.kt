package com.mambo.library

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.mambo.core.adapters.GenericStateAdapter
import com.mambo.core.adapters.LazyPagingAdapter
import com.mambo.core.adapters.getInflater
import com.mambo.core.extensions.onQueryTextChanged
import com.mambo.core.utils.*
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.data.models.Poem
import com.mambo.library.databinding.FragmentLibraryBinding
import com.mambo.library.databinding.ItemPoemLibraryBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.ocpsoft.prettytime.PrettyTime
import javax.inject.Inject

@AndroidEntryPoint
class LibraryFragment : Fragment(R.layout.fragment_library) {


    @Inject
    lateinit var libraryActions: LibraryActions


    private val sharedViewModel: MainViewModel by activityViewModels()
    private val viewModel: LibraryViewModel by viewModels()

    private val binding by viewBinding(FragmentLibraryBinding::bind)

    private val adapter = LazyPagingAdapter<Poem, ItemPoemLibraryBinding>(Poem.COMPARATOR)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_library, menu)

        val searchItem = menu.findItem(R.id.action_library_search)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged { sharedViewModel.updateLibraryQuery(it) }

        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        initViews()

        lifecycleScope.launch {
            sharedViewModel.libraryPoems.collectLatest { adapter.submitData(it) }
        }

        lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collectLatest { state: CombinedLoadStates ->
                when (state.source.refresh) {
                    is LoadState.Loading -> binding.layoutLibraryState.showLoading()
                    is LoadState.Error -> binding.layoutLibraryState.showError()
                    is LoadState.NotLoading -> {
                        if (state.append.endOfPaginationReached && adapter.itemCount < 1)
                            binding.layoutLibraryState.showEmpty()
                        else
                            binding.layoutLibraryState.showContent()

                    }
                }
            }
        }

    }

    private fun initViews() {
        binding.apply {
            toolbarLibrary.title = "Library"
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbarLibrary)
            fabCreatePoem.setOnClickListener { libraryActions.navigateToCompose() }
        }

        setupRecyclerview()

    }

    private fun setupRecyclerview() {

        adapter.apply {
            onCreate { ItemPoemLibraryBinding.inflate(it.getInflater(), it, false) }
            onBind { poem ->
                binding.apply {

                    val duration = PrettyTime().formatDuration(poem.createdAt.toDate())

                    tvPublishedTitle.text = poem.title
                    tvPublishedDuration.text = duration

                    val color = poem.topic?.color ?: "#ECF0FE"
                    layoutPoemLibrary.setBackgroundColor(Color.parseColor(color))

                }
            }
            onItemClicked { libraryActions.navigateToPoem(it) }
            withLoadStateHeaderAndFooter(
                header = GenericStateAdapter(adapter::retry),
                footer = GenericStateAdapter(adapter::retry)
            )

        }

        binding.layoutLibraryState.apply {
            tvEmpty.text = "No Poem Found"
            tvError.text = "Couldn't load Poems!"

            recyclerView.adapter = adapter
            buttonRetry.setOnClickListener { adapter.retry() }
        }

    }

}