package com.mambo.poetree.utils

import androidx.recyclerview.widget.DiffUtil
import com.mambo.poetree.data.model.Topic

class TopicUtils {

    private val emotions: MutableList<String> = ArrayList()

    companion object {
        const val LOVE = "love"
        const val NATURE = "nature"
        const val DEATH = "death"
        const val SPIRITUALITY = "spiritual"
        const val SELF = "self"
        const val RELATIONSHIPS = "relationships"
        const val MOTIVATION = "motivation"
        const val BEAUTY = "beauty"
        const val DESIRE = "desire"
        const val OTHER = "other"

        val TOPIC_COMPARATOR =
            object : DiffUtil.ItemCallback<Topic>() {
                override fun areItemsTheSame(
                    oldItem: Topic,
                    newItem: Topic
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: Topic,
                    newItem: Topic
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

    init {
        emotions.addAll(
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
            Topic(name = LOVE),
            Topic(name = MOTIVATION),
            Topic(name = SELF),
            Topic(name = DESIRE),
            Topic(name = RELATIONSHIPS),
            Topic(name = BEAUTY),
            Topic(name = DEATH),
            Topic(name = NATURE),
            Topic(name = SPIRITUALITY),
            Topic(name = OTHER),
        )
    }

}