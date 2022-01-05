package com.mambo.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mambo.core.repository.TopicsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    topicsRepository: TopicsRepository
) : ViewModel() {

    private val _eventChannel = Channel<ExploreEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val _topics = topicsRepository.topics()
    val topics = _topics.asLiveData()

    sealed class ExploreEvent {
    }

}