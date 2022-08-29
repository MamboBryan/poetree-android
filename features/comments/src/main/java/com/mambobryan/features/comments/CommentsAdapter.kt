package com.mambobryan.features.comments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.core.OnPoemClickListener
import com.mambo.data.models.Comment
import com.mambobryan.features.comments.databinding.ItemCommentBinding
import org.ocpsoft.prettytime.PrettyTime
import javax.inject.Inject

class CommentsAdapter @Inject constructor() :
    PagingDataAdapter<Comment, CommentsAdapter.CommentViewHolder>(Comment.COMPARATOR) {

    private lateinit var onPoemClickListener: OnPoemClickListener

    fun setListener(listener: OnPoemClickListener) {
        onPoemClickListener = listener
    }

    inner class CommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val prettyTime = PrettyTime()

        fun bind(comment: Comment) {
            binding.apply {


                val duration = prettyTime.formatDuration(comment.createdAt)


            }
        }

    }

    override fun onBindViewHolder(holder: CommentsAdapter.CommentViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) holder.bind(currentItem)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentsAdapter.CommentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCommentBinding.inflate(layoutInflater, parent, false)
        return CommentViewHolder(binding)
    }
}