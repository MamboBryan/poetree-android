package com.mambo.poetree.data.model

import java.util.*

data class Poem(
    val id: String,
    val title: String,
    val content: String,
    val date: Date,
    val user: User
) {
}