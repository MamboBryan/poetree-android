package com.mambobryan.libraries.searchbar.extensions

import android.view.View
import android.view.ViewTreeObserver

internal inline fun View.afterMeasured(crossinline f: View.(View) -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f(this@afterMeasured)
            }
        }
    })
}