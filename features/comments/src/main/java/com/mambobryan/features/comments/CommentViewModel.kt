package com.mambobryan.features.comments

import androidx.lifecycle.*
import com.mambo.core.repository.PoemRepository
import com.mambo.data.models.Comment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    poemsRepository: PoemRepository,
    stateHandle: SavedStateHandle
) : ViewModel() {

    private val _eventChannel = Channel<CommentsEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val _comments = poemsRepository.poems()
    val comments = _comments.asLiveData()

    private val _comment = stateHandle.getLiveData<Comment?>("comment", null)
    val comment: LiveData<Comment?> get() = _comment

    private val _content = stateHandle.getLiveData("content", "")
    val content: LiveData<String> get() = _content

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun updateComment(comment: Comment) {
        _comment.value = comment
    }

    fun onContentUpdated(text: String) {
        _content.value = text
    }

    fun onCommentSendClicked() {
        TODO("Not yet implemented")
    }

    private fun updateUi(event: CommentsEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class CommentsEvent {
    }


}