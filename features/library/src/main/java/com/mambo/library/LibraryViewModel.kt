package com.mambo.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
) : ViewModel() {

    private val _eventChannel = Channel<LibraryEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onComposeButtonClicked() = updateUi(LibraryEvent.NavigateToCompose)

    fun onPoemClicked() = updateUi(LibraryEvent.NavigateToPoem)

    private fun updateUi(event: LibraryEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class LibraryEvent {
        object NavigateToCompose : LibraryEvent()
        object NavigateToPoem : LibraryEvent()

    }

}