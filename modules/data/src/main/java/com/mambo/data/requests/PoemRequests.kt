package com.mambo.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class EditPoemRequest(
    val title: String?,
    val content: String?,
    val html: String?,
    val topic: Int?,
    val poemId: String? = null,
)

@Serializable
data class PoemRequest(
    val poemId: String,
)