package com.mambobryan.features.comments

import androidx.lifecycle.*
import com.mambo.core.repository.CommentRepository
import com.mambo.core.repository.LikeRepository
import com.mambo.data.models.Comment
import com.mambo.data.models.Poem
import com.mambo.data.preferences.UserPreferences
import com.mambo.data.requests.CreateCommentRequest
import com.mambo.data.requests.UpdateCommentRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val repository: CommentRepository,
    private val likeRepository: LikeRepository,
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

    private val _comment = MutableLiveData<Comment?>(null)
    val comment get() = _comment

    private val _deleteComment = MutableLiveData<Comment?>(null)
    private val _deletePosition = MutableLiveData<Int?>(null)

    private val _content = state.getLiveData("content", "")
    val content: LiveData<String> get() = _content

    private val _isSendingComment = MutableLiveData(false)
    val isSendingComment: LiveData<Boolean> get() = _isSendingComment

    var userId: String? = null

    init {
        viewModelScope.launch {
            userId = preferences.user.firstOrNull()?.id
        }
    }

    fun setSelectedComment(comment: Comment?) {
        _comment.value = comment
        _content.value = comment?.content
    }

    fun updateDeleteComment(comment: Comment? = null, position: Int? = null) {
        _deleteComment.value = comment
        _deletePosition.value = position
    }

    fun onContentUpdated(text: String) {
        _content.value = text
    }

    fun onCommentSendClicked() {
        when (_comment.value == null) {
            true -> createComment()
            false -> updateComment()
        }
    }

    private fun createComment() = viewModelScope.launch {
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
            updateUi(CommentsEvent.ShowError(e.localizedMessage ?: "Unable to create comment"))
            updateSendingComment()
        }
    }

    private fun updateComment() = viewModelScope.launch {

        val comment = _comment.value ?: return@launch
        val content = _content.value ?: return@launch

        if (comment.content == content) {
            updateUi(CommentsEvent.ShowError("Comment has no changes"))
            setSelectedComment(null)
            return@launch
        }

        updateSendingComment(isSending = true)

        try {

            val request = UpdateCommentRequest(commentId = comment.id, content = content)

            val response = repository.update(request = request)

            if (response.isSuccessful.not()) {
                updateUi(CommentsEvent.ShowError(response.message))
                updateSendingComment()
                return@launch
            }

            setSelectedComment(null)
            clearContent()
            updateSendingComment()
            updateUi(CommentsEvent.RefreshAdapter)
            updateUi(CommentsEvent.ShowSuccess(response.message))

        } catch (e: Exception) {
            updateUi(CommentsEvent.ShowError(e.localizedMessage ?: "Unable to update comment"))
            updateSendingComment()
        }
    }

    private fun deleteComment() = viewModelScope.launch {

        val comment = _deleteComment.value ?: return@launch

        val response = repository.delete(commentId = comment.id)

        if (response.isSuccessful.not()) {
            updateUi(CommentsEvent.ShowError(response.message))
            updateSendingComment()
            return@launch
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

    fun onCommentDelete() {
        deleteComment()
    }

    fun onCommentLiked(commentId: String) = viewModelScope.launch {

        val response = likeRepository.likeComment(commentId = commentId)

        if (response.isSuccessful.not()) {
            updateUi(CommentsEvent.ShowError(response.message))
            return@launch
        }

        updateUi(CommentsEvent.ShowSuccess(response.message))
    }

    fun onCommentUnliked(commentId: String) = viewModelScope.launch {
        val response = likeRepository.unLikeComment(commentId = commentId)

        if (response.isSuccessful.not()) {
            updateUi(CommentsEvent.ShowError(response.message))
            return@launch
        }

        updateUi(CommentsEvent.ShowSuccess(response.message))

    }

    sealed class CommentsEvent {
        data class ShowError(val message: String) : CommentsEvent()
        data class ShowSuccess(val message: String) : CommentsEvent()
        object ClearTextInput : CommentsEvent()
        object RefreshAdapter : CommentsEvent()
        object Idle : CommentsEvent()
    }


}