package com.mambo.poetree.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "emotions")
@Parcelize
data class Emotion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val emoji: Int,
    val start: String,
    val end: String
) : Parcelable