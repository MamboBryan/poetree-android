package com.mambo.core.extensions

import android.widget.TextView
import androidx.annotation.DrawableRes


fun TextView.startDrawable(@DrawableRes id: Int) = this.apply {
    setCompoundDrawablesRelativeWithIntrinsicBounds(id, 0, 0, 0)
}

fun TextView.endDrawable(@DrawableRes id: Int) = this.apply {
    setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, id, 0)
}