package com.mambo.poetree.ui.edit

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
import com.mambo.data.Poem
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentEditBinding
import com.mambo.core.adapters.ViewPagerAdapter
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class EditFragment : Fragment(R.layout.fragment_edit) {

    private val binding by viewBinding(FragmentEditBinding::bind)
    private val viewModel by viewModels<EditViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            NavigationUI.setupWithNavController(toolbarEdit, findNavController())
            toolbarEdit.setOnMenuItemClickListener { item ->

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
                    is EditViewModel.EditPoemEvent.NavigateBackWithResult -> {
                        binding.root.clearFocus()

                        setFragmentResult(
                            "create_update_request",
                            bundleOf("create_update_result" to event.result)
                        )

                        findNavController().popBackStack()
                    }

                    is EditViewModel.EditPoemEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }

                    is EditViewModel.EditPoemEvent.NavigateToComposeFragment -> {
                        navigateToComposeFragment(event.poem)
                    }

                    EditViewModel.EditPoemEvent.NavigateToPreview -> {
                        showPreview()
                    }

                    EditViewModel.EditPoemEvent.NavigateToEditView -> {
                        showEditView()
                    }
                }
            }
        }
    }

    private fun showEditView() {
        binding.apply {
            viewPagerEdit.setCurrentItem(0, true)
        }

        updateMenu()
    }

    private fun showPreview() {

        binding.apply {
            viewPagerEdit.setCurrentItem(1, true)
        }

        updateMenu()
    }

    private fun updateMenu() {
        binding.apply {

            val menu = toolbarEdit.menu
            val position = viewPagerEdit.currentItem

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
            viewPagerEdit.isUserInputEnabled = false
            viewPagerEdit.adapter = adapter
        }

    }

    private fun navigateToComposeFragment(poem: Poem) {

    }

}