package com.mambo.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mambo.core.repository.PoemRepository
import com.mambo.data.models.Poem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
) : ViewModel() {

    @Inject
    lateinit var repository: PoemRepository

    private val _message = MutableStateFlow<String?>(null)
    val message get() = _message

    fun deleteBookmark(poem: Poem) = viewModelScope.launch {
        repository.deleteBookmark(poem.toBookmark())
        _message.value = "Poem Deleted!"
        delay(2000)
        _message.value = null
    }

}