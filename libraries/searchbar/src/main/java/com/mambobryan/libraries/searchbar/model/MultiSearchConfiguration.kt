package com.mambobryan.libraries.searchbar.model

import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes

internal data class MultiSearchConfiguration(
    @DrawableRes val searchSelectionIndicator: Int,
    @StyleRes val searchTextAppearance: Int,
    @DrawableRes val searchIcon: Int,
    @DrawableRes val searchRemoveIcon: Int,
    val searchAnimationDuration: Int
)