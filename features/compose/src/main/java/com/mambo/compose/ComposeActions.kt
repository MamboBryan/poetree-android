package com.mambo.compose

import com.mambo.data.models.Poem

interface ComposeActions {
    fun navigateToPoem(poem: Poem)
}