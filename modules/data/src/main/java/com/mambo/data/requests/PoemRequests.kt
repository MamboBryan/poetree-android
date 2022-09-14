package com.mambo.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class EditPoemRequest(
    val poemId: String,
    val title: String?,
    val content: String?,
    val html: String?,
    val topic: Int?,
)

@Serializable
data class CreatePoemRequest(
    val title: String,
    val content: String,
    val html: String,
    val topic: Int,
)

@Serializable
data class PoemRequest(
    val poemId: String,
)