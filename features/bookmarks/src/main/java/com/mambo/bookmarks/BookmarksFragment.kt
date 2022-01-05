package com.mambo.bookmarks

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mambo.bookmarks.databinding.FragmentBookmarksBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class BookmarksFragment : Fragment(R.layout.fragment_bookmarks) {

    val viewModel: BookmarksViewModel by viewModels()
    val binding by viewBinding(FragmentBookmarksBinding::bind)
    val adapter by lazy { BookmarkAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    else -> {
                    }
                }
            }
        }

        viewModel.poems.observe(viewLifecycleOwner) {

            val poems = listOf("Mambo", "Tambo", "Rambo", "Sambo", "Wambo")

            binding.apply {
                layoutStateBookmarks.stateError.isVisible = it == null
                layoutStateBookmarks.stateLoading.isVisible = it == null
                layoutStateBookmarks.stateEmpty.isVisible = it.isEmpty()
                layoutStateBookmarks.stateContent.isVisible = it.isNotEmpty()

                adapter.submitList(poems)
            }

        }

    }

    private fun initViews() {
        binding.apply {
            toolbarBookmarks.title = "Bookmarks"

            layoutStateBookmarks.recyclerView.adapter = adapter
        }
    }

}