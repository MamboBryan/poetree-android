package com.mambo.data.responses

import com.mambo.data.models.PoemDto
import com.mambo.data.models.Published
import com.mambo.data.models.Topic
import com.mambo.data.models.User
import kotlinx.serialization.Serializable

@Serializable
data class PoemResponse(
    var id: String,
    var createdAt: String,
    var updatedAt: String,
    var editedAt: String?,
    var title: String,
    var content: String,
    var html: String,
    var user: User,
    var topic: Topic,
    var reads: Long,
    var read: Boolean,
    var bookmarks: Long,
    var bookmarked: Boolean,
    var likes: Long,
    var liked: Boolean,
    var comments: Long,
    var commented: Boolean
) {
    fun toPoemDto() =
        PoemDto(
            id,
            createdAt,
            updatedAt,
            editedAt,
            title,
            content,
            html,
            user,
            topic,
            reads,
            read,
            bookmarks,
            bookmarked,
            likes,
            liked,
            comments,
            commented
        )

    fun toPublished() =
        Published(
            id,
            createdAt,
            updatedAt,
            editedAt,
            title,
            content,
            html,
            user,
            topic,
            reads,
            read,
            bookmarks,
            bookmarked,
            likes,
            liked,
            comments,
            commented
        )

}