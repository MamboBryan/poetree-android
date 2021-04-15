package com.mambo.poetree.data.model

import android.os.Parcelable
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
    val emotion: String = "",
    val topic: String = "",
    val date: Date = Date(),
    val user: User
) : Parcelable {
}