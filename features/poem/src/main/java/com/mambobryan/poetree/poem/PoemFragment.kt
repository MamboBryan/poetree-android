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
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.data.models.Poem
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.getNavOptionsPopUpToCurrent
import com.mambobryan.navigation.extensions.navigate
import com.mambobryan.poetree.poem.databinding.FragmentPoemBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PoemFragment : Fragment(R.layout.fragment_poem) {

    private val sharedViewModel: MainViewModel by activityViewModels()
    private val viewModel: PoemViewModel by viewModels()

    private val binding by viewBinding(FragmentPoemBinding::bind)

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.setPoem(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.poem.observe(viewLifecycleOwner) {
            viewModel.updatePoem(it!!)
        }

        initViews()
        initEditor()

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    PoemViewModel.PoemEvent.NavigateToArtistDetails -> navigateToArtistDetails()
                    PoemViewModel.PoemEvent.NavigateToComments -> navigateToComments()
                    PoemViewModel.PoemEvent.HideLoadingDialog -> LoadingDialog.dismiss()
                    PoemViewModel.PoemEvent.ShowLoadingDialog -> {
                        LoadingDialog.show(requireContext(), false)
                    }
                    PoemViewModel.PoemEvent.NavigateToBackstack -> {
                        findNavController().popBackStack()
                    }
                    is PoemViewModel.PoemEvent.NavigateToEditPoem -> {
                        sharedViewModel.setPoem(event.poem)
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
                    is PoemViewModel.PoemEvent.TogglePoemBookmarked -> {
                        binding.apply {
                            val icon = if (event.isBookmarked) R.drawable.ic_baseline_bookmark_24
                            else R.drawable.ic_baseline_bookmark_border_24

                            ivPoemBookmark.setImageDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    icon
                                )
                            )
                        }
                    }
                    is PoemViewModel.PoemEvent.TogglePoemLiked -> {
                        binding.apply {
                            val icon = if (event.isLiked) R.drawable.ic_baseline_favorite_24
                            else R.drawable.ic_baseline_favorite_border_24

                            ivPoemLike.setImageDrawable(
                                ContextCompat.getDrawable(requireContext(), icon)
                            )
                        }
                    }
                    PoemViewModel.PoemEvent.ClearCommentEditText -> {
                        binding.layoutPoemComment.edtComment.text.clear()
                    }
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

                tvPoemLikes.text = prettyCount(poem.likes)
                tvPoemBookmarks.text = prettyCount(poem.bookmarks)
                tvPoemComments.text = prettyCount(poem.comments)
                tvPoemReads.text = prettyCount(poem.reads)

                ivPoemArtist.isVisible = poem.isMine(userId = viewModel.userId).not()
                ivPoemArtist.load(poem.user?.image) {
                    placeholder(R.drawable.ic_baseline_account_circle_24)
                    error(R.drawable.ic_baseline_account_circle_24)
                    transformations(CircleCropTransformation())
                }

            }
        }

        viewModel.comment.observe(viewLifecycleOwner) {
            binding.layoutPoemComment.apply {

                ivComment.isEnabled = !it.isNullOrEmpty()

                if (it.isNullOrEmpty())
                    ivComment.setColorFilter(getColor(R.color.primary_100))
                else
                    ivComment.setColorFilter(getColor(R.color.colorPrimary))

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
            viewModel.comments.collectLatest {
                it?.let { (commented, comments) ->
                    binding.apply {
                        tvPoemComments.text = prettyCount(comments)
                        val icon = if (commented) R.drawable.ic_baseline_mode_comment_24
                        else R.drawable.ic_outline_mode_comment_24
                        ivPoemComment.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), icon)
                        )
                    }
                }
            }
        }

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
                        showConfirmPublishDialog()
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
        ivPoemComment.setOnClickListener { viewModel.onCommentsClicked() }
        ivPoemArtist.setOnClickListener { viewModel.onArtistImageClicked() }

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

    private fun navigateToEditPoem() {
        sharedViewModel.setPoem(viewModel.poem.value)
        navigate(getDeeplink(Destinations.COMPOSE), getNavOptionsPopUpToCurrent())
    }

    private fun navigateToArtistDetails() {
        navigate(getDeeplink(Destinations.ARTIST))
    }

    private fun navigateToComments() {
        navigate(getDeeplink(Destinations.COMMENTS))
    }

}