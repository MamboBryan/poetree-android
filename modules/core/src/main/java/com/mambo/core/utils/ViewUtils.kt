package com.mambo.core.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.mambo.ui.databinding.LayoutCompleteRecyclerRefreshBinding
import com.mambo.ui.databinding.LayoutCompleteRecyclerVerticalBinding

fun View.setupFullHeight() {
    val layoutParams = this.layoutParams
    layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
    this.layoutParams = layoutParams
}

fun LayoutCompleteRecyclerVerticalBinding.showLoading(text: String? = null) {
    stateContent.isVisible = false
    stateEmpty.isVisible = false
    stateError.isVisible = false
    stateLoading.isVisible = true
    tvLoading.isVisible = text != null
    tvLoading.text = text ?: ""
}

fun LayoutCompleteRecyclerVerticalBinding.showError() {
    stateContent.isVisible = false
    stateEmpty.isVisible = false
    stateLoading.isVisible = false
    stateError.isVisible = true
}

fun LayoutCompleteRecyclerVerticalBinding.showEmpty() {
    stateContent.isVisible = false
    stateLoading.isVisible = false
    stateError.isVisible = false
    stateEmpty.isVisible = true
}

fun LayoutCompleteRecyclerVerticalBinding.showContent() {
    stateEmpty.isVisible = false
    stateLoading.isVisible = false
    stateError.isVisible = false
    stateContent.isVisible = true
}

fun LayoutCompleteRecyclerRefreshBinding.showLoading(text: String? = null) {
    stateContent.isVisible = false
    stateEmpty.isVisible = false
    stateError.isVisible = false
    stateLoading.isVisible = true
    tvLoading.isVisible = text != null
    tvLoading.text = text ?: ""
}

fun LayoutCompleteRecyclerRefreshBinding.showError() {
    stateContent.isRefreshing = false
    stateContent.isVisible = false
    stateEmpty.isVisible = false
    stateLoading.isVisible = false
    stateError.isVisible = true
}

fun LayoutCompleteRecyclerRefreshBinding.showEmpty() {
    stateContent.isRefreshing = false
    stateContent.isVisible = false
    stateLoading.isVisible = false
    stateError.isVisible = false
    stateEmpty.isVisible = true
}

fun LayoutCompleteRecyclerRefreshBinding.showContent() {
    stateContent.isRefreshing = false
    stateEmpty.isVisible = false
    stateLoading.isVisible = false
    stateError.isVisible = false
    stateContent.isVisible = true
}