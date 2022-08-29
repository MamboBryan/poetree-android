package com.mambo.core.utils

import androidx.core.view.isVisible
import com.mambo.ui.databinding.LayoutCompleteRecyclerRefreshBinding
import com.mambo.ui.databinding.LayoutCompleteRecyclerVerticalBinding

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
    stateContent.isVisible = false
    stateEmpty.isVisible = false
    stateLoading.isVisible = false
    stateError.isVisible = true
}

fun LayoutCompleteRecyclerRefreshBinding.showEmpty() {
    stateContent.isVisible = false
    stateLoading.isVisible = false
    stateError.isVisible = false
    stateEmpty.isVisible = true
}

fun LayoutCompleteRecyclerRefreshBinding.showContent() {
    stateEmpty.isVisible = false
    stateLoading.isVisible = false
    stateError.isVisible = false
    stateContent.isVisible = true
}