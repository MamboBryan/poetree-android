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
import com.google.android.material.snackbar.Snackbar
import com.like.IconType
import com.like.LikeButton
import com.like.OnLikeListener
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
                when (state.source.refresh) {
                    is LoadState.Loading -> binding.layoutComments.showLoading()
                    is LoadState.Error -> binding.layoutComments.showError()
                    is LoadState.NotLoading -> {
                        if (state.append.endOfPaginationReached && adapter.itemCount < 1)
                            binding.layoutComments.showEmpty()
                        else
                            binding.layoutComments.showContent()

                    }
                }
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
            onBind { comment ->
                binding.apply {

                    val daysAgo = Date(comment.createdAt.toDateTime() ?: 0).toDaysAgo()

                    tvCommentUser.text = comment.user.name
                    tvCommentDays.text = daysAgo
                    tvCommentContent.text = comment.content
                    tvCommentLikes.text = "${prettyCount(comment.likes)} likes"

                    val iconLike = ContextCompat.getDrawable(
                        requireContext(),
                        if (comment.liked) R.drawable.liked else R.drawable.unliked
                    )

                    val iconColor = ContextCompat.getColor(
                        requireContext(),
                        if (comment.liked) R.color.error else R.color.primary_100
                    )

                    ivCommentLike.apply {
                        isLiked = comment.liked
                        setIcon(IconType.Heart)
                        setOnLikeListener(object : OnLikeListener {
                            override fun liked(likeButton: LikeButton?) {
                                viewModel.onCommentLiked(commentId = comment.id)
                            }

                            override fun unLiked(likeButton: LikeButton?) {
                                viewModel.onCommentUnliked(commentId = comment.id)
                            }
                        })
                    }

                }
            }
            onItemClicked { }
        }

        binding.layoutComments.apply {
            recyclerView.adapter = adapter
            recyclerView.setHasFixedSize(true)
            buttonRetry.setOnClickListener { adapter.retry() }
            stateContent.setOnRefreshListener {
                recyclerView.scrollToPosition(0)
                adapter.refresh()
            }
        }

        adapter.withLoadStateHeaderAndFooter(
            header = GenericStateAdapter(adapter::retry),
            footer = GenericStateAdapter(adapter::retry)
        )

        adapter.onSwipedRight(view = binding.layoutComments.recyclerView) {
            Snackbar.make(requireView(), it.content, Snackbar.LENGTH_SHORT).show()
        }

    }

}