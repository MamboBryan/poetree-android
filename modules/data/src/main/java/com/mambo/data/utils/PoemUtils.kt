package com.mambo.data.utils

import androidx.recyclerview.widget.DiffUtil
import com.mambo.data.Poem
import com.mambo.data.User
import java.util.*

val POEM_COMPARATOR =
    object : DiffUtil.ItemCallback<Poem>() {
        override fun areItemsTheSame(oldItem: Poem, newItem: Poem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Poem, newItem: Poem): Boolean {
            return oldItem == newItem
        }
    }

fun getLocalPoem(title: String, content: String, user: User) =
    Poem(
        createdAt = Date(),
        updatedAt = Date(),
        title = title,
        content = content,
        isOffline = true,
        isPublic = false,
        userId = user.id,
        user = user
    )