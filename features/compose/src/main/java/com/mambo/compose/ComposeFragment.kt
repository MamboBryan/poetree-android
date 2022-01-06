package com.mambo.compose

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.Snackbar
import com.mambo.compose.databinding.FragmentComposeBinding
import com.mambo.core.adapters.ViewPagerAdapter
import com.mambo.data.Poem
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class ComposeFragment : Fragment(R.layout.fragment_compose) {

    private val binding by viewBinding(FragmentComposeBinding::bind)
    private val viewModel by viewModels<ComposeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            val poem = null

            NavigationUI.setupWithNavController(toolbarCompose, findNavController())
            toolbarCompose.title = if (poem == null) "Compose" else "Edit"
            toolbarCompose.setOnMenuItemClickListener { item ->

                when (item.itemId) {

                    R.id.menu_item_save -> {
                        viewModel.onSave()
                        true
                    }

                    R.id.menu_item_edit -> {
                        viewModel.onEditClicked()
                        true
                    }

                    R.id.menu_item_preview -> {
                        viewModel.onPreviewClicked()
                        true
                    }

                    R.id.menu_item_stash -> {
                        viewModel.onStash()
                        true
                    }

                    else -> {
                        false
                    }
                }


            }

        }

        setUpViewPager()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.editPoemEvent.collect { event ->
                when (event) {
                    is ComposeViewModel.ComposeEvent.NavigateBackWithResult -> {
                        binding.root.clearFocus()

                        setFragmentResult(
                            "create_update_request",
                            bundleOf("create_update_result" to event.result)
                        )

                        findNavController().popBackStack()
                    }

                    is ComposeViewModel.ComposeEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }

                    is ComposeViewModel.ComposeEvent.NavigateToEditFragment -> {
                        navigateToComposeFragment(event.poem)
                    }

                    ComposeViewModel.ComposeEvent.NavigateToPreview -> {
                        showPreview()
                    }

                    ComposeViewModel.ComposeEvent.NavigateToComposeView -> {
                        showEditView()
                    }
                }
            }
        }
    }

    private fun showEditView() {
        binding.apply { viewpagerCompose.setCurrentItem(0, true) }
        updateMenu()
    }

    private fun showPreview() {
        binding.apply { viewpagerCompose.setCurrentItem(1, true) }
        updateMenu()
    }

    private fun updateMenu() {
        binding.apply {

            val menu = toolbarCompose.menu
            val position = viewpagerCompose.currentItem

            val previewAction = menu.findItem(R.id.menu_item_preview)
            val editAction = menu.findItem(R.id.menu_item_edit)

            editAction.isVisible = position == 1
            previewAction.isVisible = position != 1

        }
    }

    private fun setUpViewPager() {

        val fragments = arrayListOf(WriteFragment(), PreviewFragment())
        val adapter = ViewPagerAdapter(fragments, childFragmentManager, lifecycle)

        binding.apply {
            viewpagerCompose.isUserInputEnabled = false
            viewpagerCompose.adapter = adapter
        }

    }

    private fun navigateToComposeFragment(poem: Poem) {

    }

}