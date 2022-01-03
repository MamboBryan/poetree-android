package com.mambo.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "topics")
@Parcelize
data class Topic(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
) : Parcelable