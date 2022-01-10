package com.mambo.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mambo.core.repository.PoemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    poemsRepository: PoemRepository
) : ViewModel() {

    private val _eventChannel = Channel<FeedEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val _poems = poemsRepository.poems()
    val poems = _poems.asLiveData()

    fun onUserImageClicked() = updateUi(FeedEvent.NavigateToProfile)

    fun onPoemClicked(poem: String) = updateUi(FeedEvent.NavigateToPoem(poem))

    fun onCreatePoemClicked() = updateUi(FeedEvent.NavigateToCompose)

    private fun updateUi(event: FeedEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class FeedEvent {
        data class NavigateToPoem(val poem: String) : FeedEvent()
        object NavigateToProfile : FeedEvent()
        object NavigateToCompose : FeedEvent()
    }

}