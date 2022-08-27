package com.mambo.data.responses

import com.mambo.data.models.Topic
import kotlinx.serialization.Serializable

@Serializable
class TopicDTO(
    val id: Int,
    val name: String,
    val color: String,
    val createdAt: String?,
    val updatedAt: String?,
)

fun TopicDTO?.toTopic(): Topic? {
    return this?.let {
        Topic(
            id = id, name = name, color = color, createdAt = createdAt, updatedAt = updatedAt
        )
    }
}

fun List<TopicDTO>.toTopicList(): List<Topic> {
    return this.map { it.toTopic()!! }
}