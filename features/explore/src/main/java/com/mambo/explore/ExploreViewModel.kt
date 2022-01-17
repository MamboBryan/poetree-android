package com.mambo.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mambo.core.repository.TopicsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    repository: TopicsRepository,
) : ViewModel() {

    private val _eventChannel = Channel<ExploreEvent>()
    val events = _eventChannel.receiveAsFlow()

    val topics = repository.getTopics().cachedIn(viewModelScope)

    fun onProfileImageClicked() = updateUi(ExploreEvent.NavigateToProfile)

    fun onSearchFieldClicked() = updateUi(ExploreEvent.NavigateToSearch)

    fun onTopicClicked() = updateUi(ExploreEvent.NavigateToSearchByTopic)

    private fun updateUi(event: ExploreEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class ExploreEvent {
        object NavigateToProfile : ExploreEvent()
        object NavigateToSearch : ExploreEvent()
        object NavigateToSearchByTopic : ExploreEvent()
    }

}