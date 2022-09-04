package com.mambo.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateCommentRequest(
    val poemId: String,
    val content: String
)

@Serializable
data class UpdateCommentRequest(
    val commentId: String,
    val content: String
)