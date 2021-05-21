package com.mambo.poetree.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.poetree.data.model.HaikuComment
import com.mambo.poetree.databinding.ItemCommentBinding
import com.mambo.poetree.utils.GradientUtils


class HaikuCommentAdapter(
) : ListAdapter<HaikuComment, HaikuCommentAdapter.HaikuCommentViewHolder>(HAIKU_TOPIC_COMPARATOR) {

    private val gradientUtils = GradientUtils()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HaikuCommentViewHolder {
        val binding =
            ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HaikuCommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HaikuCommentViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    companion object {
        private val HAIKU_TOPIC_COMPARATOR =
            object : DiffUtil.ItemCallback<HaikuComment>() {
                override fun areItemsTheSame(
                    oldItem: HaikuComment,
                    newItem: HaikuComment
                ): Boolean {
                    return oldItem.content == newItem.content
                }

                override fun areContentsTheSame(
                    oldItem: HaikuComment,
                    newItem: HaikuComment
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

    inner class HaikuCommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(haiku: HaikuComment) {
            binding.apply {

                tvComentUsername.text = haiku.username
                tvCommentContent.text = haiku.content

            }
        }

    }

}