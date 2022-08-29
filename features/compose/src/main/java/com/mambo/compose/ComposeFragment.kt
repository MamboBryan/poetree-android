package com.mambo.compose

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.Snackbar
import com.mambo.compose.databinding.FragmentComposeBinding
import com.mambo.core.adapters.ViewPagerAdapter
import com.mambo.core.viewmodel.MainViewModel
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.navigate
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ComposeFragment : Fragment(R.layout.fragment_compose) {

    private val binding by viewBinding(FragmentComposeBinding::bind)
    private val viewModel: ComposeViewModel by viewModels()
    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.updatePoem(sharedViewModel.poem.value)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.poem.observe(viewLifecycleOwner){
            viewModel.updatePoem(it)
        }

        setupNavigation()
        setupViews()
        setUpViewPager()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {

                    is ComposeViewModel.ComposeEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }

                    is ComposeViewModel.ComposeEvent.NavigateToPublish -> {
                        sharedViewModel.setPoem(event.poem)
                        navigateToPublish()
                    }

                    ComposeViewModel.ComposeEvent.NavigateToPreview -> showPreview()

                    ComposeViewModel.ComposeEvent.NavigateToComposeView -> showEditView()

                    ComposeViewModel.ComposeEvent.NavigateToBackstack -> navigateBack()

                }
            }
        }

    }

    private fun setupNavigation() = binding.apply {
        NavigationUI.setupWithNavController(toolbarCompose, findNavController())
        requireActivity().onBackPressedDispatcher.addCallback(this@ComposeFragment) {
            viewModel.onBackClicked()
        }.isEnabled
    }

    private fun setupViews() = binding.apply {

        toolbarCompose.title = if (viewModel.poem == null) "Compose" else "Edit"
        toolbarCompose.inflateMenu(R.menu.menu_compose)
        toolbarCompose.setOnMenuItemClickListener { item ->

            when (item.itemId) {

                R.id.action_compose_edit -> {
                    viewModel.onEditClicked()
                    true
                }
                R.id.action_compose_preview -> {
                    viewModel.onPreviewClicked()
                    true
                }
                R.id.action_compose_stash -> {
                    viewModel.onStash()
                    true
                }
                R.id.action_compose_choose_topic -> {
                    viewModel.onPublish()
                    true
                }
                else -> {
                    false
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

    private fun updateMenu() = binding.apply {

        val menu = toolbarCompose.menu
        val position = viewpagerCompose.currentItem

        val previewAction = menu.findItem(R.id.action_compose_preview)
        val editAction = menu.findItem(R.id.action_compose_edit)

        editAction.isVisible = position == 1
        previewAction.isVisible = position != 1

    }

    private fun setUpViewPager() {

        val fragments = arrayListOf(WriteFragment(), PreviewFragment())
        val adapter = ViewPagerAdapter(fragments, childFragmentManager, lifecycle)

        binding.apply {
            viewpagerCompose.isUserInputEnabled = false
            viewpagerCompose.adapter = adapter
        }

    }

    private fun navigateToPublish() {
        val deeplink = getDeeplink(Destinations.PUBLISH)
        navigate(deeplink)
    }

    private fun navigateBack() {
        findNavController().popBackStack()
    }

}