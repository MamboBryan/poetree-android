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
import com.mambo.core.utils.prettyCount
import com.mambo.core.viewmodel.MainViewModel
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

    private val sharedViewModel: MainViewModel by activityViewModels()
    private val viewModel: PoemViewModel by viewModels()

    private val binding by viewBinding(FragmentPoemBinding::bind)

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
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    icon
                                )
                            )
                        }
                    }
                    PoemViewModel.PoemEvent.ClearCommentEditText -> {
                        binding.layoutPoemComment.edtComment.text.clear()
                    }
                }
            }
        }

        viewModel.poem.observe(viewLifecycleOwner) {
            binding.apply {

                layoutPoemContent.isVisible = true

                editor.html = viewModel.getHtml()
                editor.setCode()
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

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.layoutPoemComment.apply {
                edtComment.isEnabled = isLoading.not()
                ivComment.isVisible = isLoading.not()
                progressComment.isVisible = isLoading
            }
        }

    }

    private fun initViews() = binding.apply {

        NavigationUI.setupWithNavController(toolbar, findNavController())
        toolbar.title = null

//        if (viewModel.isUser)
        toolbar.inflateMenu(R.menu.menu_poem)

        val publishAction = toolbar.menu.findItem(R.id.action_poem_publish)
        publishAction.isVisible = viewModel.poem.value?.isPublic ?: false

        toolbar.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener when (it.itemId) {
                R.id.action_poem_edit -> {
                    viewModel.onEditClicked()
                    true
                }
                R.id.action_poem_publish -> {
                    viewModel.onPublishClicked()
                    true
                }
                R.id.action_poem_delete -> {
                    viewModel.onDeleteClicked()
                    true
                }
                else -> false
            }
        }

        layoutPoemActions.isVisible = viewModel.isOnline

        layoutPoemActions.isVisible = true

        ivPoemLike.setOnClickListener { viewModel.onLikeClicked() }
//        tvPoemLikes.text = prettyCount(viewModel.poem.value?.likesCount!!)
        tvPoemLikes.text = prettyCount(2000)

        ivPoemBookmark.setOnClickListener { viewModel.onBookmarkClicked() }
        tvPoemBookmarks.text = prettyCount(200000)

        ivPoemComment.setOnClickListener { viewModel.onCommentsClicked() }
        tvPoemComments.text = prettyCount(200)
        tvPoemReads.text = prettyCount(2000000)

        ivPoemArtist.setOnClickListener { viewModel.onArtistImageClicked() }

        layoutPoemComment.apply {
//            layoutCommentRoot.isVisible = viewModel.isOnline
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
        sharedViewModel.setPoem(null)
    }

}