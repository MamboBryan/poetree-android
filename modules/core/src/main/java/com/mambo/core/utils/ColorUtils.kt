package com.mambo.core.utils

import android.graphics.Color
import kotlin.math.max
import kotlin.math.min


fun lighten(color: Int, fraction: Double): Int {
    var red: Int = Color.red(color)
    var green: Int = Color.green(color)
    var blue: Int = Color.blue(color)
    red = lightenColor(red, fraction)
    green = lightenColor(green, fraction)
    blue = lightenColor(blue, fraction)
    val alpha: Int = Color.alpha(color)
    return Color.argb(alpha, red, green, blue)
}

fun darken(color: Int, fraction: Double): Int {
    var red: Int = Color.red(color)
    var green: Int = Color.green(color)
    var blue: Int = Color.blue(color)
    red = darkenColor(red, fraction)
    green = darkenColor(green, fraction)
    blue = darkenColor(blue, fraction)
    val alpha: Int = Color.alpha(color)
    return Color.argb(alpha, red, green, blue)
}

private fun getRandomPastel() {

    val red = (0 until 128).random().plus(127f)
    val blue = (0 until 128).random().plus(127f)
    val green = (0 until 128).random().plus(127f)

//    val hex = String.format("#%02x%02x%02x", r, g, b)

}

private fun darkenColor(color: Int, fraction: Double): Int {
    return max(color - color * fraction, 0.0).toInt()
}

private fun lightenColor(color: Int, fraction: Double): Int {
    return min(color + color * fraction, 255.0).toInt()
}