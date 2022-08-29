package com.mambo.data.models

import androidx.recyclerview.widget.DiffUtil
import java.util.*

data class Comment(
    val id: Int?,
    val createdAt: Date?,
    val user: User,
    val content: String
){
    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem == newItem
            }
        }
    }
}