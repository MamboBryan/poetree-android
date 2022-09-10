package com.mambo.compose

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.irozon.alertview.AlertActionStyle
import com.irozon.alertview.AlertStyle
import com.irozon.alertview.AlertView
import com.irozon.alertview.objects.AlertAction
import com.irozon.sneaker.Sneaker
import com.mambo.compose.databinding.FragmentComposeBinding
import com.mambo.core.adapters.ViewPagerAdapter
import com.mambo.core.utils.LoadingDialog
import com.mambo.core.utils.toObliviousHumanLanguage
import com.mambo.data.models.Poem
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ComposeFragment : Fragment(R.layout.fragment_compose) {

    private val binding by viewBinding(FragmentComposeBinding::bind)
    private val viewModel: ComposeViewModel by viewModels()

    @Inject
    lateinit var composeActions: ComposeActions

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupViews()
        setUpViewPager()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    ComposeViewModel.ComposeEvent.NavigateToBackstack -> navigateBack()
                    ComposeViewModel.ComposeEvent.HideLoading -> LoadingDialog.dismiss()
                    ComposeViewModel.ComposeEvent.ShowLoading -> LoadingDialog.show(requireContext())
                    is ComposeViewModel.ComposeEvent.ShowInvalidInputMessage -> showError(event.message)
                    is ComposeViewModel.ComposeEvent.NavigateToPublish -> navigateToPublish(event.poem)
                    is ComposeViewModel.ComposeEvent.NavigateToPoem -> navigateToPoem(event.poem)
                    is ComposeViewModel.ComposeEvent.ShowError -> showError(event.message)
                    is ComposeViewModel.ComposeEvent.ShowSuccess -> showSuccess(event.message)
                }
            }
        }

    }

    private fun showError(message: String) {
        val alert = AlertView(
            title = "Error",
            message = "\n${message.toObliviousHumanLanguage()}\n",
            style = AlertStyle.DIALOG
        )
        alert.addAction(AlertAction("dismiss", AlertActionStyle.DEFAULT) {})
        alert.show(requireActivity() as AppCompatActivity)
    }

    private fun showSuccess(message: String) {
        Sneaker.with(requireActivity())
            .setIcon(R.drawable.ic_baseline_check_circle_24)
            .setMessage(message)
            .setTitle("Success")
            .sneakSuccess()
    }

    private fun setupNavigation() = binding.apply {
        NavigationUI.setupWithNavController(toolbarCompose, findNavController())
        requireActivity().onBackPressedDispatcher.addCallback(this@ComposeFragment) {
            viewModel.onBackClicked()
        }.isEnabled
    }

    private fun setupViews() = binding.apply {
        toolbarCompose.title = if (viewModel.poem.value == null) "Compose" else "Update"
        toolbarCompose.inflateMenu(R.menu.menu_compose)
        toolbarCompose.setOnMenuItemClickListener { item ->
            when (item.itemId) {

                R.id.action_compose_edit -> {
                    showEditView()
                    true
                }
                R.id.action_compose_preview -> {
                    showPreview()
                    true
                }
                R.id.action_compose_stash -> {
                    viewModel.onStash()
                    true
                }
                R.id.action_compose_publish -> {
                    showConfirmPublishDialog()
                    true
                }
                R.id.action_compose_delete -> {
                    showConfirmDeleteDialog()
                    true
                }
                else -> {
                    false
                }
            }
        }
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

    private fun showEditView() {
        binding.apply { viewpagerCompose.setCurrentItem(0, true) }
        updateMenu()
    }

    private fun showPreview() {
        binding.apply { viewpagerCompose.setCurrentItem(1, true) }
        updateMenu()
    }

    private fun showConfirmDeleteDialog() {
        val alert = AlertView(
            "Delete Poem!",
            "You are about to delete this poem. Do you wish you to continue?",
            AlertStyle.IOS
        )
        alert.addAction(AlertAction("Yes", AlertActionStyle.NEGATIVE) {
            viewModel.onDeleteConfirmed()
        })
        alert.show(requireActivity() as AppCompatActivity)
    }

    private fun showConfirmPublishDialog() {
        val alert = AlertView(
            "Publish Your Art!",
            "You are about to publish this poem. Do you wish you to continue?",
            AlertStyle.IOS
        )
        alert.addAction(AlertAction("Yes", AlertActionStyle.NEGATIVE) {
            viewModel.onPublishConfirmed()
        })
        alert.show(requireActivity() as AppCompatActivity)
    }

    private fun navigateToPoem(poem: Poem) {
        composeActions.navigateToPoem(poem)
    }

    private fun navigateToPublish(poem: Poem) {
        composeActions.navigateToPoem(poem)
    }

    private fun navigateBack() {
        findNavController().popBackStack()
    }

}