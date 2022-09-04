package com.mambo.bookmarks

import com.mambo.data.models.Poem

interface BookmarkActions {
    fun navigateToPoem(poem: Poem)
}