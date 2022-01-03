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
    val title: String,
    val content: String,
    val emotionName: String = "",
    val topicName: String = "",
    @ColumnInfo(defaultValue = "")
    val topic: Topic? = null,
    val date: Date = Date(),
    val user: User
) : Parcelable