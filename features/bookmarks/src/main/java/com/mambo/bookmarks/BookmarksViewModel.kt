package com.mambo.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mambo.core.repository.PoemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
): ViewModel() {

    private val _eventChannel = Channel<BookmarkEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onSearchClicked() = updateUi(BookmarkEvent.ToggleSearchEditText)

    fun onSortClicked() = updateUi(BookmarkEvent.OpenFilterDialog)

    fun onPoemClicked() = updateUi(BookmarkEvent.NavigateToPoem)

    private fun updateUi(event: BookmarkEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class BookmarkEvent {
        object ToggleSearchEditText : BookmarkEvent()
        object NavigateToPoem : BookmarkEvent()
        object OpenFilterDialog : BookmarkEvent()
    }

}