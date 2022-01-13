package com.mambo.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity(tableName = "poems")
@Parcelize
data class Poem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val createdAt: Date?,
    val updatedAt: Date?,
    val title: String?,
    val content: String?,
    @ColumnInfo(defaultValue = "")
    val topic: Topic? = null,
    val userId: String?,
    val user: User?,
    val likesCount: Int =0,
    val readsCount: Int =0,
    val commentsCount: Int = 0,
    val bookmarksCount: Int = 0,
    val isLiked:Boolean = false,
    val isRead: Boolean = false,
    val isCommented: Boolean = false,
    val isBookmarked: Boolean = false,
    val isOffline: Boolean?,
    val isPublic: Boolean?
) : Parcelable