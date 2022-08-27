package com.mambo.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class TopicRequest(
    val name: String,
    val color: String
)