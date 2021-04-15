package com.mambo.poetree.utils

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import com.mambo.poetree.data.model.Emotion

class EmotionsUtils {

    private val emotions: MutableList<String> = ArrayList()

    companion object {
        const val HAPPINESS = "happiness"
        const val SADNESS = "sadness"
        const val ANGER = "anger"
        const val FEAR = "fear"
        const val DISGUST = "disgust"
        const val SURPRISE = "surprise"
    }

    init {
        emotions.addAll(arrayListOf(HAPPINESS, SADNESS, ANGER, FEAR, DISGUST, SURPRISE))
    }

    fun getEmotions(): MutableList<Emotion> {

        return arrayListOf(
            Emotion(
                name = HAPPINESS,
                start = "#FBAB7E",
                end = "#F7CE68",
                emoji = 0x1f606
            ),
            Emotion(
                name = SADNESS,
                start = "#00c6ff",
                end = "#0072ff",
                emoji = 0x1f61e
            ),
            Emotion(
                name = ANGER,
                start = "#f85032",
                end = "#e73827",
                emoji = 0x1f620
            ),
            Emotion(
                name = FEAR,
                start = "#232526",
                end = "#414345",
                emoji = 0x1f628
            ),
            Emotion(
                name = DISGUST,
                start = "#41295a",
                end = "#2f0743",
                emoji = 0x1f922
            ),
            Emotion(
                name = SURPRISE,
                start = "#43e97b",
                end = "#38f9d7",
                emoji = 0x1f632
            )
        )
    }

    fun getBackgroundByEmotion(emotion: String): GradientDrawable {

        var start = "#A9C9FF"
        var end = "#FFBBEC"

        when (emotion) {
            HAPPINESS -> {
                start = "#FBAB7E"
                end = "#F7CE68"
            }
            SADNESS -> {
                start = "#00c6ff"
                end = "#0072ff"
            }
            ANGER -> {
                start = "#f85032"
                end = "#e73827"
            }
            FEAR -> {
                start = "#232526"
                end = "#414345"
            }
            DISGUST -> {
                start = "#41295a"
                end = "#2f0743"
            }
            SURPRISE -> {
                start = "#43e97b"
                end = "#38f9d7"
            }
        }

        val gd = GradientDrawable(
            GradientDrawable.Orientation.BL_TR,
            intArrayOf(
                Color.parseColor(start),
                Color.parseColor(end)
            )
        )

        gd.cornerRadius = 0f

        return gd
    }

}