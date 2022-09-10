package com.mambobryan.poetree.poem

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.snackbar.Snackbar
import com.irozon.alertview.AlertActionStyle
import com.irozon.alertview.AlertStyle
import com.irozon.alertview.AlertView
import com.irozon.alertview.objects.AlertAction
import com.irozon.sneaker.Sneaker
import com.mambo.core.utils.LoadingDialog
import com.mambo.core.utils.prettyCount
import com.mambo.data.models.Poem
import com.mambobryan.poetree.poem.databinding.FragmentPoemBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class PoemFragment : Fragment(R.layout.fragment_poem) {

    private val viewModel: PoemViewModel by viewModels()
    private val binding by viewBinding(FragmentPoemBinding::bind)

    @Inject
    lateinit var poemActions: PoemActions

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initEditor()

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    PoemViewModel.PoemEvent.HideLoadingDialog -> LoadingDialog.dismiss()
                    PoemViewModel.PoemEvent.ShowLoadingDialog -> LoadingDialog.show(requireContext())
                    PoemViewModel.PoemEvent.NavigateToBackstack -> navigateToBackstack()
                    PoemViewModel.PoemEvent.ClearCommentEditText -> clearCommentText()
                    is PoemViewModel.PoemEvent.SneakSuccess -> {
                        Sneaker.with(requireActivity())
                            .setTitle(event.message)
                            .sneakSuccess()
                    }
                    is PoemViewModel.PoemEvent.ShowSnackBarError -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG)
                            .setAction("retry") { viewModel.onCommentSendClicked() }
                            .show()
                    }
                    is PoemViewModel.PoemEvent.ShowError -> showError(message = event.message)
                    is PoemViewModel.PoemEvent.SneakError -> {
                        Sneaker.with(requireActivity()).setMessage(event.message).sneakError()
                    }
                    is PoemViewModel.PoemEvent.ShowSuccess -> showSuccess(event.message)
                }
            }
        }

        viewModel.poem.observe(viewLifecycleOwner) { poem ->
            binding.apply {

                layoutPoemContent.isVisible = true
                layoutPoemActions.isVisible = poem.isLocal().not() == true
                layoutPoemComment.root.isVisible = poem.isLocal().not() == true

                editor.html = viewModel.getHtml()
                editor.setCode()

                updateMenu(poem)

                ivPoemArtist.isVisible = poem.isMine(userId = viewModel.userId).not()
                ivPoemArtist.load(poem.user?.image) {
                    placeholder(R.drawable.ic_baseline_account_circle_24)
                    error(R.drawable.ic_baseline_account_circle_24)
                    transformations(CircleCropTransformation())
                }

                when (poem.type) {
                    Poem.Type.LOCAL -> {}
                    Poem.Type.BOOKMARK -> viewModel.getPoemUpdate()
                    Poem.Type.REMOTE -> {
                        if (poem.read.not()) startReadTimer()
                        if (poem.bookmarked) viewModel.saveLocalBookmark()
                    }
                }


            }
        }

        viewModel.comment.observe(viewLifecycleOwner) {
            binding.layoutPoemComment.apply {

                ivComment.isEnabled = !it.isNullOrEmpty()

                if (it.isNullOrEmpty()) ivComment.setColorFilter(getColor(R.color.primary_100))
                else ivComment.setColorFilter(getColor(R.color.colorPrimary))

            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.layoutPoemComment.apply {
                edtComment.isEnabled = isLoading.not()
                ivComment.isVisible = isLoading.not()
                progressComment.isVisible = isLoading
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.reads.collectLatest {
                it?.let { (read, reads) ->
                    binding.apply {
                        editor.html = viewModel.getHtml()
                        editor.setCode()
                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.likes.collectLatest {
                it?.let { (liked, likes) ->
                    binding.apply {
                        tvPoemLikes.text = prettyCount(likes)
                        val icon = when (liked) {
                            true -> R.drawable.ic_baseline_favorite_24
                            false -> R.drawable.ic_baseline_favorite_border_24
                        }
                        ivPoemLike.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), icon)
                        )
                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.bookmarks.collectLatest {
                it?.let { (bookmarked, bookmarks) ->
                    binding.apply {
                        tvPoemBookmarks.text = prettyCount(bookmarks)
                        val icon = when (bookmarked) {
                            true -> R.drawable.ic_baseline_bookmark_24
                            false -> R.drawable.ic_baseline_bookmark_border_24
                        }
                        ivPoemBookmark.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), icon)
                        )
                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.comments.collectLatest {
                it?.let { (commented, comments) ->
                    binding.apply {
                        tvPoemComments.text = prettyCount(comments)
                        val icon = when (commented) {
                            true -> R.drawable.ic_baseline_mode_comment_24
                            false -> R.drawable.ic_outline_mode_comment_24
                        }
                        ivPoemComment.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), icon)
                        )
                    }
                }
            }
        }

    }

    private fun clearCommentText() {
        binding.layoutPoemComment.edtComment.text.clear()
    }

    private fun getColor(id: Int) = ContextCompat.getColor(requireContext(), id)

    private fun updateMenu(poem: Poem) {
        binding.apply {

            // TODO: change to new menu provider implementation
//            val menuHost = binding.toolbar as MenuHost
//            menuHost.addMenuProvider(
//                object : MenuProvider {
//                    override fun onPrepareMenu(menu: Menu) {
//                        super.onPrepareMenu(menu)
//                    }
//
//                    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                        menuInflater.inflate(R.menu.menu_poem, menu)
//                    }
//
//                    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                        return when (menuItem.itemId) {
//                            R.id.action_poem_edit -> {
//                                navigateToEditPoem()
//                                true
//                            }
//                            R.id.action_poem_publish -> {
//                                showConfirmPublishDialog()
//                                true
//                            }
//                            R.id.action_poem_delete -> {
//                                showConfirmDeleteDialog()
//                                true
//                            }
//                            else -> false
//                        }
//                    }
//
//                    override fun onMenuClosed(menu: Menu) {
//                        super.onMenuClosed(menu)
//                    }
//                },
//                viewLifecycleOwner,
//                Lifecycle.State.RESUMED
//            )

            if (poem.isLocal() or poem.isMine(viewModel.userId)) {
                toolbar.inflateMenu(R.menu.menu_poem)
                val publish = toolbar.menu.findItem(R.id.action_poem_publish)
                publish.isVisible = poem.isLocal()
            }

            toolbar.setOnMenuItemClickListener {
                return@setOnMenuItemClickListener when (it.itemId) {
                    R.id.action_poem_edit -> {
                        navigateToEditPoem()
                        true
                    }
                    R.id.action_poem_publish -> {
                        if (poemDetailsAreValid()) showConfirmPublishDialog()
                        true
                    }
                    R.id.action_poem_delete -> {
                        showConfirmDeleteDialog()
                        true
                    }
                    else -> false
                }
            }

        }
    }

    private fun initViews() = binding.apply {

        NavigationUI.setupWithNavController(toolbar, findNavController())
        toolbar.title = null

        ivPoemLike.setOnClickListener { viewModel.onLikeClicked() }
        ivPoemBookmark.setOnClickListener { viewModel.onBookmarkClicked() }
        ivPoemComment.setOnClickListener { navigateToComments() }
        ivPoemArtist.setOnClickListener { navigateToArtistDetails() }

        layoutPoemComment.apply {
            edtComment.doAfterTextChanged { viewModel.onCommentUpdated(it.toString()) }
            ivComment.setOnClickListener { viewModel.onCommentSendClicked() }
        }

    }

    private fun initEditor() = binding.apply {

        val textColor = ContextCompat.getColor(requireContext(), R.color.color_on_background)
        val backgroundColor = ContextCompat.getColor(requireContext(), R.color.color_background)

        editor.setInputEnabled(false)
        editor.setEditorFontColor(textColor)
        editor.setEditorBackgroundColor(backgroundColor)
        editor.setPadding(24, 32, 24, 16)

    }

    private fun poemDetailsAreValid(): Boolean {

        val poem = viewModel.poem.value

        if (poem?.topic == null) {
            showError(
                title = "No Topic Found!",
                message = "Poem Must has a topic to be published!"
            )
            return false
        }

        return true

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

    private fun showError(title: String = "Error", message: String) {
        val alert = AlertView(title, message, AlertStyle.DIALOG)
        alert.addAction(AlertAction("dismiss", AlertActionStyle.NEGATIVE) {})
        alert.show(requireActivity() as AppCompatActivity)
    }

    private fun showSuccess(message: String) {
        val alert = AlertView(
            "Success!",
            message,
            AlertStyle.DIALOG
        )
        alert.addAction(AlertAction("dismiss", AlertActionStyle.POSITIVE) {})
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

    private fun startReadTimer() {
        lifecycleScope.launchWhenResumed {
            delay(7500)
            if (isVisible) viewModel.onPoemRead()
        }
    }

    private fun navigateToBackstack() {
        findNavController().popBackStack()
    }

    private fun navigateToEditPoem() {
        poemActions.navigateToCompose(viewModel.poem.value!!)
    }

    private fun navigateToArtistDetails() {
        poemActions.navigateToArtist(viewModel.poem.value!!.user!!)
    }

    private fun navigateToComments() {
        poemActions.navigateToComments(viewModel.poem.value!!)
    }

}