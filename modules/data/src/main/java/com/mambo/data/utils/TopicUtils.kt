package com.mambo.data.utils

import androidx.recyclerview.widget.DiffUtil
import com.mambo.data.models.Topic

fun Topic?.isNull() = this == null

fun Topic?.isNotNull() = this != null

val TOPIC_COMPARATOR =
    object : DiffUtil.ItemCallback<Topic>() {
        override fun areItemsTheSame(oldItem: Topic, newItem: Topic): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Topic, newItem: Topic): Boolean {
            return oldItem == newItem
        }
    }

fun getTopic(name: String) = Topic(name = name, color = "#C09BD8")

class TopicUtils {

    private val topics: MutableList<String> = ArrayList()

    companion object {
        const val LOVE = "love"
        const val SELF = "self"
        const val OTHER = "other"
        const val DEATH = "death"
        const val DESIRE = "desire"
        const val BEAUTY = "beauty"
        const val NATURE = "nature"
        const val MOTIVATION = "motivation"
        const val SPIRITUALITY = "spiritual"
        const val RELATIONSHIPS = "relationships"
    }

    init {
        topics.addAll(
            arrayListOf(
                LOVE,
                MOTIVATION,
                NATURE,
                DEATH,
                SPIRITUALITY,
                SELF,
                BEAUTY,
                DESIRE,
                RELATIONSHIPS,
                OTHER
            )
        )
    }

    fun getTopics(): MutableList<Topic> {

        return arrayListOf(
            getTopic(name = LOVE),
            getTopic(name = MOTIVATION),
            getTopic(name = SELF),
            getTopic(name = DESIRE),
            getTopic(name = RELATIONSHIPS),
            getTopic(name = BEAUTY),
            getTopic(name = DEATH),
            getTopic(name = NATURE),
            getTopic(name = SPIRITUALITY),
            getTopic(name = OTHER),
        )
    }

}