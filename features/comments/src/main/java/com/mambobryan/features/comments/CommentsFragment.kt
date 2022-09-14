package com.mambobryan.features.comments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.mambo.core.adapters.GenericStateAdapter
import com.mambo.core.adapters.LazyPagingAdapter
import com.mambo.core.adapters.getInflater
import com.mambo.core.utils.*
import com.mambo.data.models.Comment
import com.mambobryan.features.comments.databinding.FragmentCommentsBinding
import com.mambobryan.features.comments.databinding.ItemCommentBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@AndroidEntryPoint
class CommentsFragment : Fragment(R.layout.fragment_comments) {

    private val binding by viewBinding(FragmentCommentsBinding::bind)
    private val viewModel: CommentViewModel by viewModels()

    private val adapter = LazyPagingAdapter<Comment, ItemCommentBinding>(Comment.COMPARATOR)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        viewModel.poem.observe(viewLifecycleOwner) {
            it?.let {
                binding.apply {
                    tvCommentsTitle.text = it.title
                    tvCommentsCount.text = "Comments ( ${prettyCount(it.comments)} )"
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.comments.observe(viewLifecycleOwner) {
                if (it != null) adapter.submitData(lifecycle, it)
            }
        }

        lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collectLatest { state: CombinedLoadStates ->
                binding.layoutComments.apply {
                    when (state.source.refresh) {
                        is LoadState.Loading -> showLoading()
                        is LoadState.Error -> showError()
                        is LoadState.NotLoading -> {
                            if (state.append.endOfPaginationReached && adapter.itemCount < 1)
                                showEmpty()
                            else
                                showContent()

                        }
                    }
                }
            }
        }

        viewModel.comment.observe(viewLifecycleOwner) {
            binding.layoutCommentsEdit.apply {
                layoutEditing.isVisible = it != null
                edtComment.setText(it?.content)
            }
        }

        viewModel.content.observe(viewLifecycleOwner) {
            binding.layoutCommentsEdit.apply {

                ivComment.isEnabled = !it.isNullOrEmpty()

                if (it.isNullOrEmpty())
                    ivComment.setColorFilter(
                        ContextCompat.getColor(requireContext(), R.color.primary_100)
                    )
                else
                    ivComment.setColorFilter(
                        ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                    )

            }
        }

        viewModel.isSendingComment.observe(viewLifecycleOwner) { isLoading ->
            binding.layoutCommentsEdit.apply {
                layoutEditing.isEnabled = isLoading.not()
                edtComment.isEnabled = isLoading.not()
                ivComment.isVisible = isLoading.not()
                progressComment.isVisible = isLoading
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.events.collectLatest { event ->
                when (event) {
                    CommentViewModel.CommentsEvent.Idle -> {}
                    CommentViewModel.CommentsEvent.RefreshAdapter -> adapter.refresh()
                    CommentViewModel.CommentsEvent.ClearTextInput -> {
                        binding.layoutCommentsEdit.edtComment.setText("")
                    }
                    is CommentViewModel.CommentsEvent.ShowError -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }
                    is CommentViewModel.CommentsEvent.ShowSuccess -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT).show()
                        adapter.refresh()
                    }
                }
            }
        }

    }

    private fun initViews() = binding.apply {
        toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        layoutCommentsEdit.apply {
            ivCommentClose.setOnClickListener { viewModel.setSelectedComment(null) }
            edtComment.doAfterTextChanged { viewModel.onContentUpdated(it.toString()) }
            ivComment.setOnClickListener { viewModel.onCommentSendClicked() }
        }
        layoutComments.apply {
            tvEmpty.text = "No Comments Added"
            tvError.text = "Couldn't get Comments."
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {

        adapter.apply {
            onCreate { ItemCommentBinding.inflate(it.getInflater(), it, false) }
            onBind { comment: Comment, position: Int ->
                binding.apply {

                    val daysAgo = Date(comment.createdAt.toDateTime() ?: 0).toDaysAgo()

                    tvCommentUser.text = comment.user.name
                    tvCommentDays.text = daysAgo
                    tvCommentContent.text = comment.content
                    tvCommentLikes.text = "${prettyCount(comment.likes)} likes"

                    val isMyComment = comment.user.id == viewModel.userId
                    tvCommentUpdate.isVisible = isMyComment
                    tvCommentDelete.isVisible = isMyComment
                    tvCommentEdited.apply {
                        isVisible = comment.updatedAt != null
                        text = " \u2022 Edited"

                    }
                    tvCommentUpdate.setOnClickListener {
                        viewModel.setSelectedComment(comment)
                    }

                    tvCommentDelete.setOnClickListener {
                        viewModel.updateDeleteComment(comment, position)
                        adapter.remove(comment)
                        showDeleteSnackBar(comment, position)
                    }

                    val likeIcon = when (comment.liked) {
                        true -> com.mambo.ui.R.drawable.liked
                        false -> com.mambo.ui.R.drawable.unliked
                    }

                    ivCommentLike.apply {
                        setImageDrawable(ContextCompat.getDrawable(requireContext(), likeIcon))
                        setColorFilter(R.color.primary)
                        setOnClickListener {

                            val updatedComment = comment.copy(
                                liked = comment.liked.not(),
                                likes = when (comment.liked) {
                                    true -> comment.likes - 1
                                    false -> comment.likes + 1
                                }
                            )

                            adapter.update(item = updatedComment, index = position)

                            when (comment.liked) {
                                true -> viewModel.onCommentUnliked(commentId = comment.id)
                                false -> viewModel.onCommentLiked(commentId = comment.id)
                            }
                        }
                    }

                }
            }
            withLoadStateHeaderAndFooter(
                header = GenericStateAdapter(adapter::retry),
                footer = GenericStateAdapter(adapter::retry)
            )
        }

        binding.layoutComments.apply {
            buttonRetry.setOnClickListener { adapter.retry() }
            stateContent.setOnRefreshListener { adapter.refresh() }
            recyclerView.adapter = adapter
        }

    }

    private fun showDeleteSnackBar(comment: Comment, position: Int) {
        val snack = Snackbar.make(requireView(), "Comment Deleted", Snackbar.LENGTH_SHORT)
        snack.setAction("UNDO") {
            adapter.add(item = comment, index = position)
            viewModel.updateDeleteComment(null, null)
        }
        snack.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onShown(transientBottomBar: Snackbar?) {
                super.onShown(transientBottomBar)
            }

            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                when (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                    true -> viewModel.onCommentDelete()
                    false -> viewModel.updateDeleteComment(null, null)
                }
            }
        })
        snack.show()
    }

}