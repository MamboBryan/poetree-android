package com.mambobryan.poetree.poem

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.Snackbar
import com.irozon.alertview.AlertActionStyle
import com.irozon.alertview.AlertStyle
import com.irozon.alertview.AlertView
import com.irozon.alertview.objects.AlertAction
import com.irozon.sneaker.Sneaker
import com.mambo.core.utils.LoadingDialog
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.libraries.editor.WYSIWYG
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.getNavOptionsPopUpToCurrent
import com.mambobryan.navigation.extensions.navigate
import com.mambobryan.poetree.poem.databinding.FragmentPoemBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PoemFragment : Fragment(R.layout.fragment_poem) {

    private val binding by viewBinding(FragmentPoemBinding::bind)
    private val viewModel: PoemViewModel by viewModels()
    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.updatePoem(sharedViewModel.poem.value!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initEditor()

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    PoemViewModel.PoemEvent.NavigateToArtistDetails -> navigateToArtistDetails()
                    PoemViewModel.PoemEvent.NavigateToComments -> navigateToComments()
                    PoemViewModel.PoemEvent.HideLoadingDialog -> LoadingDialog.dismiss()
                    PoemViewModel.PoemEvent.ShowLoadingDialog -> LoadingDialog.show(
                        requireContext(),
                        false
                    )
                    PoemViewModel.PoemEvent.NavigateToBackstack -> {
                        findNavController().popBackStack()
                    }
                    is PoemViewModel.PoemEvent.NavigateToEditPoem -> {
                        sharedViewModel.updatePoem(event.poem)
                        navigateToEditPoem()
                    }
                    is PoemViewModel.PoemEvent.ShowPoemDeleteDialog -> {
                        showConfirmDeleteDialog()
                    }
                    is PoemViewModel.PoemEvent.ShowSuccessSneaker -> {
                        Sneaker.with(requireActivity())
                            .setTitle(event.message)
                            .sneakSuccess()
                    }
                    is PoemViewModel.PoemEvent.ShowSnackBarError -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG)
                            .setAction("retry") { viewModel.onCommentSendClicked() }
                            .show()
                    }
                }
            }
        }

        viewModel.comment.observe(viewLifecycleOwner) {
            binding.layoutPoemComment.apply {

                ivComment.isEnabled = !it.isNullOrEmpty()

                if (it.isNullOrEmpty())
                    ivComment.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.primary_100
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )
                else
                    ivComment.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimary
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )

            }
        }

    }

    private fun initViews() = binding.apply {

        NavigationUI.setupWithNavController(toolbar, findNavController())
        toolbar.title = null

        if (viewModel.isUser) toolbar.inflateMenu(R.menu.menu_poem)

        toolbar.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener when (it.itemId) {
                R.id.action_poem_edit -> {
                    viewModel.onEditClicked()
                    true
                }
                R.id.action_poem_delete -> {
                    viewModel.onDeleteClicked()
                    true
                }
                R.id.action_poem_publish -> {
                    viewModel.onPublishClicked()
                    true
                }
                R.id.action_poem_unpublish -> {
                    viewModel.onUnPublishClicked()
                    true
                }
                else -> false
            }
        }

        layoutArtist.isVisible = viewModel.poem?.userId == "1"
        layoutPoemActions.isVisible = viewModel.isOnline

        layoutArtist.setOnClickListener { viewModel.onArtistImageClicked() }
        ivPoemComment.setOnClickListener { viewModel.onCommentsClicked() }

        layoutPoemComment.apply {
            layoutCommentRoot.isVisible = viewModel.isOnline
            edtComment.doAfterTextChanged { viewModel.onCommentUpdated(it.toString()) }
            ivComment.setOnClickListener { viewModel.onCommentSendClicked() }
        }

    }

    private fun initEditor() = binding.apply {
        val wysiwygEditor = editor
        val textColor = ContextCompat.getColor(requireContext(), R.color.color_on_background)
        val backgroundColor = ContextCompat.getColor(requireContext(), R.color.color_background)

        wysiwygEditor.setInputEnabled(false)
        wysiwygEditor.setEditorFontColor(textColor)
        wysiwygEditor.setEditorBackgroundColor(backgroundColor)
        wysiwygEditor.setPadding(16, 16, 16, 16)

        wysiwygEditor.html = viewModel.getHtml()
        wysiwygEditor.setCode()

        wysiwygEditor.setOnInitialLoadListener(object : WYSIWYG.AfterInitialLoadListener {
            override fun onAfterInitialLoad(isReady: Boolean) {
                layoutPoemLoading.isVisible = !isReady
                layoutPoemContent.isVisible = isReady
            }
        })
    }

    private fun showConfirmDeleteDialog() {
        val alert = AlertView(
            "Delete Poem!",
            "You are about to delete this poem. Do you wish you to continue?",
            AlertStyle.IOS
        )
        alert.addAction(AlertAction("Yes", AlertActionStyle.DEFAULT) {
            viewModel.onDeleteConfirmed()
        })
        alert.show(requireActivity() as AppCompatActivity)
    }

    private fun navigateToEditPoem() {
        navigate(getDeeplink(Destinations.COMPOSE), getNavOptionsPopUpToCurrent())
    }

    private fun navigateToArtistDetails() {
        navigate(getDeeplink(Destinations.ARTIST))
    }

    private fun navigateToComments() {
        navigate(getDeeplink(Destinations.COMMENTS))
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.updatePoem(null)
    }

}