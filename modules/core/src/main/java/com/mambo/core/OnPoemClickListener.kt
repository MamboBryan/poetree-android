package com.mambo.core

import com.mambo.data.models.Poem

abstract interface OnPoemClickListener {
    fun onPoemClicked(poem: Poem)
}