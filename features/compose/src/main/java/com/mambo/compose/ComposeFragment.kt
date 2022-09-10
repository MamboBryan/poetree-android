package com.mambo.compose

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
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

    override fun onResume() {
        super.onResume()

        initViews()

    }

    private fun initViews() = binding.apply {
        edtTitle.setText(viewModel.poemTitle)
        editor.html = viewModel.poemContent
    }

    private fun setupViews() = binding.apply {

        toolbarCompose.title = if (viewModel.poem.value == null) "Compose" else "Update"
        toolbarCompose.inflateMenu(R.menu.menu_compose)
        toolbarCompose.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_compose_preview,
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

        edtTitle.doAfterTextChanged { title -> viewModel.poemTitle = title.toString() }

        val textColor = ContextCompat.getColor(requireContext(), R.color.color_on_background)
        val backgroundColor = ContextCompat.getColor(requireContext(), R.color.color_background)

        editor.setEditorFontSize(16)
        editor.setEditorFontColor(textColor)
        editor.setEditorBackgroundColor(backgroundColor)
        editor.setPadding(16, 16, 16, 16)

        editor.setPlaceholder("Pen down thoughts here...")

        actionUndo.setOnClickListener { editor.undo() }

        actionRedo.setOnClickListener { editor.redo() }

        actionBold.setOnClickListener { editor.setBold() }

        actionItalic.setOnClickListener { editor.setItalic() }

        actionUnderline.setOnClickListener { editor.setUnderline() }

        actionIndent.setOnClickListener { editor.setIndent() }

        actionOutdent.setOnClickListener { editor.setOutdent() }

        actionAlignCenter.setOnClickListener { editor.setAlignCenter() }

        actionAlignLeft.setOnClickListener { editor.setAlignLeft() }

        actionAlignRight.setOnClickListener { editor.setAlignRight() }

        actionAlignJustify.setOnClickListener { editor.setAlignJustifyFull() }

        actionBlockquote.setOnClickListener { editor.setBlockquote() }

        actionHeading.setOnClickListener { editor.setHeading(4) }

        editor.onTextChanged { content, html ->
            viewModel.poemContent = content ?: ""
            viewModel.poemHtml = html ?: ""
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