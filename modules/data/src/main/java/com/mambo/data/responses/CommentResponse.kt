package com.mambo.data.responses

import com.mambo.data.models.Comment
import com.mambo.data.models.MinimalUser
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    val id: String,
    val createdAt: String,
    val updatedAt: String?,
    val content: String,
    val userId: String,
    val poemId: String
)

@Serializable
data class CompleteCommentResponse(
    val id: String,
    val createdAt: String,
    val updatedAt: String?,
    val content: String,
    val poemId: String,
    val user: MinimalUser,
    val liked: Boolean,
    val likes: Long
) {

    fun toComment() = Comment(
        id, createdAt, updatedAt, content, poemId, user, liked, likes
    )

}