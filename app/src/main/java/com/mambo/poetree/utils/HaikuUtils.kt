package com.mambo.poetree.utils

import com.mambo.poetree.data.model.Haiku
import com.mambo.poetree.data.model.HaikuComment
import com.mambo.poetree.data.model.HaikuEmotion
import com.mambo.poetree.data.model.HaikuTopic

class HaikuUtils {

    val haikus: MutableList<Haiku> = ArrayList()
    val haikuTopics: MutableList<HaikuTopic> = ArrayList()
    val haikuEmotions: MutableList<HaikuEmotion> = ArrayList()
    val haikuComments: MutableList<HaikuComment> = ArrayList()

    init {

        haikuTopics.add(HaikuTopic("love"))
        haikuTopics.add(HaikuTopic("hate"))
        haikuTopics.add(HaikuTopic("death"))
        haikuTopics.add(HaikuTopic("breakup"))
        haikuTopics.add(HaikuTopic("political"))
        haikuTopics.add(HaikuTopic("romance"))

        haikuEmotions.add(HaikuEmotion("happiness"))
        haikuEmotions.add(HaikuEmotion("sadness"))
        haikuEmotions.add(HaikuEmotion("disgust"))
        haikuEmotions.add(HaikuEmotion("loathing"))
        haikuEmotions.add(HaikuEmotion("skiddish"))

        haikuComments.add(
            HaikuComment(
                "TheRadBrad",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
            )
        )
        haikuComments.add(
            HaikuComment(
                "TheRadBrad",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
            )
        )
        haikuComments.add(
            HaikuComment(
                "Kajungu",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
            )
        )
        haikuComments.add(
            HaikuComment(
                "Mzoboshe",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
            )
        )
        haikuComments.add(
            HaikuComment(
                "Alandria",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
            )
        )

        haikus.add(
            Haiku(
                "The Monarch",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
                "Mambo"
            )
        )

        haikus.add(
            Haiku(
                "Tenderly Active",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
                "Yonder"
            )
        )

        haikus.add(
            Haiku(
                "Shoot In The Rain",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
                "Kaluki"
            )
        )

        haikus.add(
            Haiku(
                "Commodore's Ring",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
                "Busarazin"
            )
        )

        haikus.add(
            Haiku(
                "It Means Life",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
                "ubuntu"
            )
        )

        haikus.add(
            Haiku(
                "I Found Love At War",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
                "TellyTale"
            )
        )

        haikus.add(
            Haiku(
                "Suprise Suprise",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
                "Mandehoek"
            )
        )

        haikus.add(
            Haiku(
                "Brilliant Future",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
                "Kronos"
            )
        )

        haikus.add(
            Haiku(
                "Hard to Live in Poverty",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
                "Simple Man"
            )
        )
    }


}