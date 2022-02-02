package com.mambo.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.mambo.core.repository.PoemRepository
import com.mambo.data.models.Poem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    poemsRepository: PoemRepository,
) : ViewModel() {

    private val _eventChannel = Channel<FeedEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val _poems = poemsRepository.poems()
    val poems = _poems.asLiveData()

    val feeds = poemsRepository.getLocalPoems("").cachedIn(viewModelScope)
    val locals = Pager(
        config = PagingConfig(pageSize = 10, prefetchDistance = 2),
        pagingSourceFactory = { poemsRepository.getPoems() }
    ).flow

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