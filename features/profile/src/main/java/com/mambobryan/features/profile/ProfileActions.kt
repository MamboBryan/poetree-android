package com.mambobryan.features.profile

import com.mambo.data.models.Poem

interface ProfileActions {

    fun navigateToPoem(poem: Poem)

}