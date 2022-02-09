package com.mambo.library

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.mambo.core.OnPoemClickListener
import com.mambo.core.adapters.GenericStateAdapter
import com.mambo.core.extensions.onQueryTextChanged
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.data.models.Poem
import com.mambo.library.databinding.FragmentLibraryBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.navigate
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LibraryFragment : Fragment(R.layout.fragment_library) {

    private val sharedViewModel: MainViewModel by activityViewModels()
    private val viewModel: LibraryViewModel by viewModels()

    private val binding by viewBinding(FragmentLibraryBinding::bind)

    @Inject
    lateinit var adapter: LibraryAdapter

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

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    LibraryViewModel.LibraryEvent.NavigateToCompose -> navigateToCompose()
                    LibraryViewModel.LibraryEvent.NavigateToPoem -> navigateToPoem()
                }
            }
        }

        lifecycleScope.launch {
            sharedViewModel.unpublishedPoems.collectLatest { adapter.submitData(it) }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadState ->
                binding.layoutLibraryState.apply {

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


                        }
                    }
                }
            }
        }

    }

    private fun initViews() {
        binding.apply {
            toolbarLibrary.title = "Library"
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbarLibrary)

            fabCreatePoem.setOnClickListener { viewModel.onComposeButtonClicked() }

            layoutLibraryState.apply {
                tvEmpty.text = "No Poem Found"
                tvError.text = "Couldn't load Poems!"

                recyclerView.adapter = adapter
                buttonRetry.setOnClickListener { adapter.retry() }
            }

        }

        adapter.setListener(object : OnPoemClickListener {
            override fun onPoemClicked(poem: Poem) {
                sharedViewModel.setPoem(poem)
                viewModel.onPoemClicked()
            }
        })

        adapter.withLoadStateHeaderAndFooter(
            header = GenericStateAdapter(adapter::retry),
            footer = GenericStateAdapter(adapter::retry)
        )
    }

    private fun navigateToCompose() {
        navigate(getDeeplink(Destinations.COMPOSE))
    }

    private fun navigateToPoem() {
        navigate(getDeeplink(Destinations.POEM))
    }

}