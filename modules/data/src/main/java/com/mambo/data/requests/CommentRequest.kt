package com.mambo.data.requests

data class CreateCommentRequest(
    val poemId: String,
    val content: String
)

data class UpdateCommentRequest(
    val commentId: String,
    val content: String
)