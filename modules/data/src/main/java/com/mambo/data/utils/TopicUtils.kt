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

class TopicUtils {

    companion object {
        const val LOVE = "love"
        const val WAR = "war"
        const val DEATH = "death"
        const val NATURE = "nature"
        const val BEAUTY = "beauty"
        const val AGING = "aging"
        const val DESIRE = "desire"
        const val DREAMS = "dreams"
        const val COMING_OF_AGE = "coming of age"
        const val DESTINY = "destiny"
        const val DEPRESSION = "depression"
        const val COURAGE = "courage"
        const val SPIRITUALITY = "spirituality"
        const val HAPPINESS = "happiness"
        const val GOD = "god"
        const val FRIENDSHIP = "friendship"
        const val HEARTBREAK = "heartbreak"
        const val LOSS_TRAGEDY = "loss"
        const val MEMORIES = "memories"
        const val MORTALITY = "mortality"
        const val IMMORALITY = "immorality"
        const val MARRIAGE = "marriage"
        const val REBIRTH = "rebirth"
        const val SEASONS = "seasons"
        const val SEXUALITY = "sexuality"
        const val RELIGION = "religion"
        const val SECRETS = "secrets"
        const val PEACE = "peace"
        const val PAIN = "pain"
        const val ISOLATION = "isolation"
        const val FORGIVENESS = "forgiveness"
        const val HATRED = "hatred"
        const val ART = "art"
        const val ANXIETY = "anxiety"
        const val DEFEAT = "defeat"
        const val JOY = "joy"
        const val LIFE = "life"
        const val TRUST = "trust"
        const val GENDER = "gender"
        const val PURPOSE = "purpose"
        const val HISTORY = "history"
        const val JEALOUSY = "jealousy"
        const val CHANGE = "change"
        const val INNOCENCE = "innocence"
        const val OPPORTUNITY = "opportunity"
        const val OTHER = "other"
    }


    fun getTopics(): MutableList<Topic> {

        val topics = mutableListOf<Topic>()

        topics.add(Topic(name = LOVE, color = "#55FE71E2"))
        topics.add(Topic(name = MARRIAGE, color = "#55FB8A74"))
        topics.add(Topic(name = PEACE, color = "#551FD2FF"))
        topics.add(Topic(name = DESIRE, color = "#55FFADBB"))
        topics.add(Topic(name = BEAUTY, color = "#55FF6FFF"))
        topics.add(Topic(name = JOY, color = "#55FFFF00"))
        topics.add(Topic(name = DREAMS, color = "#558FFF1F"))
        topics.add(Topic(name = GOD, color = "#55D3A0F8"))
        topics.add(Topic(name = FRIENDSHIP, color = "#55FF8247"))

        topics.add(Topic(name = ISOLATION, color = "#5547DAFF"))
        topics.add(Topic(name = HAPPINESS, color = "#55FFEB00"))
        topics.add(Topic(name = MEMORIES, color = "#55FF875C"))
        topics.add(Topic(name = DEATH, color = "#55A3A3A3"))
        topics.add(Topic(name = HEARTBREAK, color = "#55C3AFFD"))
        topics.add(Topic(name = DEPRESSION, color = "#5500F5F5"))
        topics.add(Topic(name = AGING, color = "#55FF70BA"))
        topics.add(Topic(name = NATURE, color = "#5536FC71"))
        topics.add(Topic(name = WAR, color = "#55A3A3A3"))
        topics.add(Topic(name = SPIRITUALITY, color = "#5570FFFF"))

        topics.add(Topic(name = PAIN, color = "#55B8B8B8"))
        topics.add(Topic(name = ANXIETY, color = "#551FFFFF"))
        topics.add(Topic(name = SECRETS, color = "#55FE72DB"))
        topics.add(Topic(name = LIFE, color = "#556CFF0A"))
        topics.add(Topic(name = COMING_OF_AGE, color = "#5570E2FF"))
        topics.add(Topic(name = DESTINY, color = "#55FF9D70"))
        topics.add(Topic(name = COURAGE, color = "#55FFF600"))
        topics.add(Topic(name = JEALOUSY, color = "#55C69AD5"))

        topics.add(Topic(name = PURPOSE, color = "#55FFB999"))
        topics.add(Topic(name = HATRED, color = "#55FFD6DB"))
        topics.add(Topic(name = LOSS_TRAGEDY, color = "#55CCCCCC"))
        topics.add(Topic(name = FORGIVENESS, color = "#5570DBFF"))
        topics.add(Topic(name = IMMORALITY, color = "#55FFA6C9"))
        topics.add(Topic(name = SEASONS, color = "#5566FF66"))
        topics.add(Topic(name = GENDER, color = "#55C58AD0"))
        topics.add(Topic(name = TRUST, color = "#55FFF347"))
        topics.add(Topic(name = SEXUALITY, color = "#55ADFFD6"))

        topics.add(Topic(name = RELIGION, color = "#55CB8DF6"))
        topics.add(Topic(name = DEFEAT, color = "#5547FFFF"))
        topics.add(Topic(name = OPPORTUNITY, color = "#55FEFE71"))
        topics.add(Topic(name = HISTORY, color = "#55FFA585"))
        topics.add(Topic(name = INNOCENCE, color = "#55B290DF"))
        topics.add(Topic(name = CHANGE, color = "#5547C2FF"))
        topics.add(Topic(name = MORTALITY, color = "#55FC89AC"))
        topics.add(Topic(name = REBIRTH, color = "#557AF500"))
        topics.add(Topic(name = OTHER, color = "#55D6E1FF"))

        return topics
    }

}