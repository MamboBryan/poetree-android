package com.mambo.poetree.utils

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import com.mambo.poetree.data.model.GradientColor

class GradientUtils {

    private val gradients: MutableList<GradientColor> = ArrayList()

    init {

        gradients.add(GradientColor("#4facfe", "#00f2fe"))
        gradients.add(GradientColor("#c471f5", "#fa71cd"))
        gradients.add(GradientColor("#43e97b", "#43e97b"))
        gradients.add(GradientColor("#fa709a", "#fee140"))
        gradients.add(GradientColor("#A9C9FF", "#FFBBEC"))
        gradients.add(GradientColor("#ff9a9e", "#fecfef"))
        gradients.add(GradientColor("#FF5ACD", "#FBDA61"))
        gradients.add(GradientColor("#a1c4fd", "#c2e9fb"))
        gradients.add(GradientColor("#84fab0", "#8fd3f4"))
        gradients.add(GradientColor("#4facfe", "#00f2fe"))

    }

    companion object{

        fun getGradientBackground(): GradientDrawable {

            val gd = GradientDrawable(
                GradientDrawable.Orientation.BL_TR,
                intArrayOf(
                    Color.parseColor("#4facfe"),
                    Color.parseColor("#00f2fe")
                )
            )

            gd.cornerRadius = 0f

            return gd
        }
    }

    fun getGradientBackground(position: Int): GradientDrawable {

        val index = position % 10

        val gd = GradientDrawable(
            GradientDrawable.Orientation.BL_TR,
            intArrayOf(
                Color.parseColor(gradients[index].start),
                Color.parseColor(gradients[index].end)
            )
        )

        gd.cornerRadius = 0f

        return gd
    }
}