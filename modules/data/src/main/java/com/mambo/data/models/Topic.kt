package com.mambo.data.models

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity(tableName = "topics")
@Parcelize
@Serializable
data class Topic(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String,
    val color: String,
    val createdAt: String?,
    val updatedAt: String?,
) : Parcelable {
    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<Topic>() {
            override fun areItemsTheSame(oldItem: Topic, newItem: Topic): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: Topic, newItem: Topic): Boolean {
                return oldItem == newItem
            }
        }
    }
}