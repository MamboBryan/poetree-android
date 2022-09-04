package com.mambobryan.features.comments

import androidx.lifecycle.*
import com.mambo.core.repository.CommentRepository
import com.mambo.data.models.Poem
import com.mambo.data.preferences.UserPreferences
import com.mambo.data.requests.CreateCommentRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val repository: CommentRepository,
    preferences: UserPreferences,
    state: SavedStateHandle
) : ViewModel() {

    private val _events = MutableStateFlow<CommentsEvent>(CommentsEvent.Idle)
    val events: StateFlow<CommentsEvent> get() = _events

    val poem = state.getLiveData<Poem?>("poem", null)

    private val _comments = poem.switchMap {
        if (it != null)
            repository.getComments(it.id).asLiveData()
        else
            MutableLiveData(null)

    }
    val comments = _comments

    private val _content = state.getLiveData("content", "")
    val content: LiveData<String> get() = _content

    private val _isSendingComment = MutableLiveData(false)
    val isSendingComment: LiveData<Boolean> get() = _isSendingComment

    var userId: String? = null

    init {
        viewModelScope.launch {Poem
            userId = preferences.user.firstOrNull()?.id
        }
    }

    fun updatePoem(poem: Poem) = viewModelScope.launch {
        this@CommentViewModel.poem.value = poem
    }

    fun onContentUpdated(text: String) {
        _content.value = text
    }

    fun onCommentSendClicked() = viewModelScope.launch {
        updateSendingComment(isSending = true)
        try {

            val request = CreateCommentRequest(
                poemId = poem.value!!.id,
                content = _content.value!!
            )

            val response = repository.create(request = request)

            if (response.isSuccessful.not()) {
                updateUi(CommentsEvent.ShowError(response.message))
                updateSendingComment()
                return@launch
            }

            clearContent()
            updateSendingComment()
            updateUi(CommentsEvent.RefreshAdapter)
            updateUi(CommentsEvent.ShowSuccess(response.message))
        } catch (e: Exception) {
            updateUi(CommentsEvent.ShowError(e.localizedMessage))
            updateSendingComment()
        }
    }

    private fun clearContent() {
        _content.value = ""
        updateUi(CommentsEvent.ClearTextInput)
    }

    private fun updateSendingComment(isSending: Boolean = false) {
        _isSendingComment.value = isSending
    }

    private fun updateUi(event: CommentsEvent) = viewModelScope.launch {
        _events.value = event
    }

    sealed class CommentsEvent {
        data class ShowError(val message: String) : CommentsEvent()
        data class ShowSuccess(val message: String) : CommentsEvent()
        object ClearTextInput : CommentsEvent()
        object RefreshAdapter : CommentsEvent()
        object Idle : CommentsEvent()
    }


}