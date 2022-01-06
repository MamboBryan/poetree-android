package com.mambobryan.features.comments

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
class CommentViewModel @Inject constructor(
    poemsRepository: PoemRepository
): ViewModel() {

    private val _eventChannel = Channel<CommentsEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val _comments = poemsRepository.poems()
    val comments = _comments.asLiveData()

    private fun updateUi(event: CommentsEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class CommentsEvent {
    }


}