package com.mambo.data.models

import java.util.*

data class Comment(
    val id: Int?,
    val createdAt: Date?,
    val user: User,
    val content: String
)