package com.mambo.data.requests

import com.mambo.data.models.Topic

data class CreatePoemRequest(
    val title: String,
    val content: String,
    val topic: Topic
)