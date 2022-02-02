package com.mambo.data.utils

import androidx.recyclerview.widget.DiffUtil
import com.mambo.data.models.Comment

val COMMENT_COMPARATOR = object : DiffUtil.ItemCallback<Comment>() {
    override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem == newItem
    }
}