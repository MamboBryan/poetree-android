package com.mambo.library

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.mambo.core.OnPoemClickListener
import com.mambo.core.adapters.GenericStateAdapter
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.data.models.Poem
import com.mambo.library.databinding.FragmentGenericLibraryBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PublishedFragment : Fragment(R.layout.fragment_generic_library) {

    private val sharedViewModel: MainViewModel by activityViewModels()
    private val viewModel: LibraryViewModel by viewModels({ requireParentFragment() })
    private val binding by viewBinding(FragmentGenericLibraryBinding::bind)

    @Inject
    lateinit var adapter: LibraryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        lifecycleScope.launch {
            sharedViewModel.publicPoems.collectLatest { adapter.submitData(it) }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadState ->
                binding.layoutStateLibrary.apply {

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

    private fun initViews() {
        binding.apply {

            layoutStateLibrary.apply {
                buttonRetry.setOnClickListener { adapter.retry() }
                recyclerView.adapter = adapter
                recyclerView.setHasFixedSize(true)
                stateContent.setOnRefreshListener {
                    recyclerView.scrollToPosition(0)
                    adapter.refresh()
                }
            }

        }

        adapter.isPublic(true)
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

}