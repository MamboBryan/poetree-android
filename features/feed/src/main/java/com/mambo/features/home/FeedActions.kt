package com.mambo.features.home

import com.mambo.data.models.Poem

interface FeedActions {
    fun navigateToPoem(poem: Poem)
    fun navigateToProfile()
    fun navigateToSettings()
    fun navigateToCompose()
}