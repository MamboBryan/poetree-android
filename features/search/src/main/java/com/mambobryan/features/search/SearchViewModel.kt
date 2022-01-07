package com.mambobryan.features.search

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
class SearchViewModel @Inject constructor(
    poemsRepository: PoemRepository
): ViewModel() {

    private val _eventChannel = Channel<SearchEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val _poems = poemsRepository.poems()
    val poems = _poems.asLiveData()

    private fun updateUi(event: SearchEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class SearchEvent{

    }

}