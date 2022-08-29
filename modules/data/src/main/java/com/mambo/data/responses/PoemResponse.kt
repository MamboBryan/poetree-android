package com.mambo.data.responses

import com.mambo.data.models.Topic
import com.mambo.data.models.User
import kotlinx.serialization.Serializable

@Serializable
data class PoemResponse(
    var id: String? = null,
    var createdAt: String? = null,
    var updatedAt: String? = null,
    var editedAt: String? = null,
    var title: String? = null,
    var content: String? = null,
    var html: String? = null,
    var user: User? = null,
    var topic: Topic? = null,
    var reads: Int? = null,
    var read: Boolean? = null,
    var bookmarks: Int? = null,
    var bookmarked: Boolean? = null,
    var likes: Int? = null,
    var liked: Boolean? = null,
    var comments: Int? = null,
    var commented: Boolean? = null
)