package com.mambo.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mambo.core.repository.PoemRepository
import com.mambo.data.models.Poem
import com.mambo.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    preferences: UserPreferences
) : ViewModel() {

    private val _eventChannel = Channel<FeedEvent>()
    val events = _eventChannel.receiveAsFlow()

    val imageUrl = preferences.imageUrl

    fun onUserImageClicked() = updateUi(FeedEvent.NavigateToProfile)

    fun onPoemClicked(poem: Poem) = updateUi(FeedEvent.NavigateToPoem(poem))

    fun onCreatePoemClicked() = updateUi(FeedEvent.NavigateToCompose)

    fun onSettingsClicked() = updateUi(FeedEvent.NavigateToSettings)

    private fun updateUi(event: FeedEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class FeedEvent {
        data class NavigateToPoem(val poem: Poem) : FeedEvent()
        object NavigateToProfile : FeedEvent()
        object NavigateToCompose : FeedEvent()
        object NavigateToSettings : FeedEvent()
    }

}