package com.mambo.bookmarks

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mambo.bookmarks.databinding.FragmentBookmarksBinding
import com.mambo.core.OnPoemClickListener
import com.mambo.data.models.Poem
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.navigate
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class BookmarksFragment : Fragment(R.layout.fragment_bookmarks) {

    private val viewModel: BookmarksViewModel by viewModels()
    private val binding by viewBinding(FragmentBookmarksBinding::bind)
    private val adapter by lazy { BookmarkAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    BookmarksViewModel.BookmarkEvent.NavigateToPoem -> navigateToPoem()
                    BookmarksViewModel.BookmarkEvent.OpenFilterDialog -> {
                    }
                    BookmarksViewModel.BookmarkEvent.ToggleSearchEditText -> {
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
            toolbarBookmarks.inflateMenu(R.menu.menu_bookmarks)
            toolbarBookmarks.setOnMenuItemClickListener { item ->
                return@setOnMenuItemClickListener when (item.itemId) {
                    R.id.action_item_search -> {
                        true
                    }
                    else -> {
                        true
                    }
                }
            }

            layoutStateBookmarks.recyclerView.adapter = adapter
            adapter.setListener(object : OnPoemClickListener {
                override fun onPoemClicked(poem: Poem) {
                    viewModel.onPoemClicked()
                }
            })
        }
    }

    private fun navigateToPoem() {
        val deeplink = getDeeplink(Destinations.POEM)
        navigate(deeplink)
    }
}