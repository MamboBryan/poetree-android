package com.mambo.data.models

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.*

@Parcelize
@Serializable
open class Poem(
    open val id: String,
    open val title: String,
    open val content: String,
    open val html: String,
    open val createdAt: String,
    open val updatedAt: String,
    open val topic: Topic? = null,
) : Parcelable {

    open var user: User? = null
    open var reads: Long = 0
    open var read: Boolean = false
    open var bookmarks: Long = 0
    open var bookmarked: Boolean = false
    open var likes: Long = 0L
    open var liked: Boolean = false
    open var comments: Long = 0L
    open var commented: Boolean = false

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<Poem>() {
            override fun areItemsTheSame(
                oldItem: Poem,
                newItem: Poem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Poem,
                newItem: Poem
            ): Boolean {
                return oldItem.id == newItem.id && oldItem.content == newItem.content &&
                        oldItem.title == newItem.title
            }
        }
    }

    fun isLocal() = user == null

    fun isMine(userId: String?) = user?.id == userId

    fun copy(
        title: String? = null,
        content: String? = null,
        html: String? = null,
        createdAt: String? = null,
        updatedAt: String? = null,
        topic: Topic? = null,
    ): Poem = Poem(
        id = this.id,
        title = title.takeIf { it != null } ?: this.title,
        content = content.takeIf { it != null } ?: this.content,
        html = html.takeIf { it != null } ?: this.html,
        createdAt = createdAt.takeIf { it != null } ?: this.createdAt,
        updatedAt = updatedAt.takeIf { it != null } ?: this.updatedAt,
        topic = topic.takeIf { it != null } ?: this.topic,
    )

    fun toLocalPoem() = LocalPoem(
        id = this.id,
        title = this.title,
        content = this.content,
        html = this.html,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        topic = this.topic,
    )

}

@Entity(tableName = "poems")
@Parcelize
data class LocalPoem(
    @PrimaryKey(autoGenerate = false)
    override val id: String = UUID.randomUUID().toString(),
    override val title: String,
    override val content: String,
    override val html: String,
    override val createdAt: String,
    override val updatedAt: String,
    override val topic: Topic?
) : Parcelable, Poem(
    id = id,
    title = title,
    content = content,
    html = html,
    createdAt = createdAt,
    updatedAt = updatedAt,
    topic = topic
)

data class PoemDto(
    override val id: String,
    override val createdAt: String,
    override val updatedAt: String,
    val editedAt: String?,
    override val title: String,
    override val content: String,
    override val html: String,
    override var user: User?,
    override val topic: Topic,
    override var reads: Long,
    override var read: Boolean,
    override var bookmarks: Long,
    override var bookmarked: Boolean,
    override var likes: Long,
    override var liked: Boolean,
    override var comments: Long,
    override var commented: Boolean
) : Poem(id, title, content, html, createdAt, updatedAt, topic)