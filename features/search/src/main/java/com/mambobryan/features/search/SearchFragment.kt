package com.mambobryan.features.search

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
import com.mambo.core.adapters.PoemPagingAdapter
import com.mambo.core.utils.*
import com.mambo.core.viewmodel.MainViewModel
import com.mambobryan.features.search.databinding.FragmentSearchBinding
import com.mambobryan.libraries.searchbar.model.MultiSearchChangeListener
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.navigate
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject


@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private val sharedViewModel: MainViewModel by activityViewModels()
    private val viewModel: SearchViewModel by viewModels()

    private val binding by viewBinding(FragmentSearchBinding::bind)

    @Inject
    lateinit var adapter: PoemPagingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setupSearchView()

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    SearchViewModel.SearchEvent.NavigateToPoem -> navigateToPoem()
                    is SearchViewModel.SearchEvent.UpdatePoemInSharedViewModel -> {
                        sharedViewModel.setPoem(event.poem)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            sharedViewModel.searchedPoems.collectLatest { adapter.submitData(it) }
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

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.setTopic(null)
    }

    private fun setupSearchView() = binding.apply {
        val topic = sharedViewModel.topic.value

        if (topic == null) {
            searchViewSearch.performClick()
            searchViewSearch.requestFocus()
            showKeyboard(searchViewSearch)
        }

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
        binding.apply {

            NavigationUI.setupWithNavController(toolbarSearch, findNavController())
            toolbarSearch.title = null

            layoutPoems.apply {
                recyclerView.adapter = adapter
                recyclerView.addOnScrollListener(object :
                    RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        if (dy > 50) {
                            fabSearch.show()
                        } else {
                            fabSearch.hide()
                        }
                        super.onScrolled(recyclerView, dx, dy)
                    }
                })
            }
        }

        adapter.onPoemClicked { viewModel.onPoemClicked(poem = it) }

        adapter.withLoadStateHeaderAndFooter(
            header = GenericStateAdapter(adapter::retry),
            footer = GenericStateAdapter(adapter::retry)
        )

    }

    private fun navigateToPoem() {
        val deeplink = getDeeplink(Destinations.POEM)
        navigate(deeplink)
    }

}