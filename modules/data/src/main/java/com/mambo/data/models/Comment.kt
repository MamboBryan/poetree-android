package com.mambo.data.models

import androidx.recyclerview.widget.DiffUtil
import java.util.*

data class Comment(
    val id: String,
    val createdAt: String,
    val updatedAt: String?,
    val content: String,
    val poemId: String,
    val user: MinimalUser,
    val liked: Boolean,
    val likes: Long
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