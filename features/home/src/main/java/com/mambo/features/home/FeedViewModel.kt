package com.mambo.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(): ViewModel() {

    private val _eventChannel = Channel<FeedEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onUserImageClicked() = updateUi(FeedEvent.NavigateToAccountDetails)
    fun onPoemClicked(poem: String) = updateUi(FeedEvent.NavigateToPoem(poem))

    fun onCreatePoemClicked() = updateUi(FeedEvent.NavigateToCreatePoem)

    private fun updateUi(event: FeedEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class FeedEvent {
        data class NavigateToPoem(val poem: String) : FeedEvent()
        object NavigateToAccountDetails : FeedEvent()
        object NavigateToCreatePoem : FeedEvent()
    }

}