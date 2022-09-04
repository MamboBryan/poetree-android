package com.mambobryan.poetree.poem

import com.mambo.data.models.Poem
import com.mambo.data.models.User

interface PoemActions {

    fun navigateToComments(poem: Poem)
    fun navigateToCompose(poem: Poem)
    fun navigateToArtist(user: User)

}