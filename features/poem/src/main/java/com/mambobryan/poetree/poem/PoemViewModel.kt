package com.mambobryan.poetree.poem

import androidx.lifecycle.*
import com.mambo.core.repository.CommentRepository
import com.mambo.core.repository.PoemRepository
import com.mambo.core.utils.toDate
import com.mambo.data.models.Poem
import com.mambo.data.preferences.UserPreferences
import com.mambo.data.requests.CreateCommentRequest
import com.mambo.data.responses.CommentResponse
import com.mambo.data.responses.UserDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.ocpsoft.prettytime.PrettyTime
import javax.inject.Inject

@HiltViewModel
class PoemViewModel @Inject constructor(
    private val poemRepository: PoemRepository,
    private val commentRepository: CommentRepository,
    private val preferences: UserPreferences,
    state: SavedStateHandle
) : ViewModel() {

    private val _eventChannel = Channel<PoemEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val _poem = state.getLiveData<Poem>("poem")
    val poem: LiveData<Poem> get() = _poem

    private val _comment = MutableLiveData("")
    val comment: LiveData<String> get() = _comment

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _comments = MutableStateFlow<Pair<Boolean, Long>?>(null)
    val comments: StateFlow<Pair<Boolean, Long>?> get() = _comments

    var userDetails: UserDetails? = null
    var userId: String? = null

    init {
        viewModelScope.launch {
            userDetails = preferences.user.firstOrNull().also {
                userId = it?.id
            }

            _comments.value = Pair(_poem.value!!.commented, _poem.value!!.comments)

        }
    }

    fun getHtml(): String {
        val html = StringBuilder()
        val prettyTime = PrettyTime()

        val topic = _poem.value?.topic?.name ?: "Topicless"
        val duration = prettyTime.formatDuration(_poem.value?.createdAt.toDate())

        html.append("$topic • $duration ")
        html.append("<h2><b> ${_poem.value?.title}</b></h2>")
        html.append("By • ${_poem.value?.user?.name ?: "Me"}")
        html.append("<br><br>")
        html.append("<i>${_poem.value?.content}</i>")
        html.append("<br><br>")

        return html.toString()
    }

    fun onCommentUpdated(text: String) {
        _comment.value = text
    }

    fun onCommentSendClicked() = viewModelScope.launch {
        _isLoading.value = true
        try {

            val request = CreateCommentRequest(
                poemId = poem.value!!.id,
                content = comment.value!!
            )

            val response = commentRepository.create(request = request)

            if (response.isSuccessful.not()) {
                updateUi(PoemEvent.ShowSnackBarError(response.message))
                _isLoading.value = false
                return@launch
            }

            _comment.value = ""
            _isLoading.value = false
            _comments.value = _comments.value?.let { (_, comments) -> Pair(true, comments + 1) }

            updateUi(PoemEvent.ShowSuccessSneaker(response.message))
            updateUi(PoemEvent.ClearCommentEditText)
        } catch (e: Exception) {
            _isLoading.value = false
            updateUi(PoemEvent.ShowSnackBarError("Failed Sending Comment"))
        }
    }

    fun onDeleteConfirmed() {

        val isRemote = _poem.value?.isLocal()?.not() ?: false

        if (isRemote)
            deleteFromRemote()
        else
            deleteFromLocal()
    }

    fun onPublishConfirmed() = viewModelScope.launch {
        showLoading()
        try {
            delay(2000)
            hideLoading()
        } catch (e: Exception) {
            hideLoading()
        }
    }

    fun onLikeClicked() = viewModelScope.launch {
        updateUi(PoemEvent.TogglePoemLiked(true))
        try {
            //TODO network call to like/unlike
            delay(2000)
        } catch (e: Exception) {
            updateUi(PoemEvent.TogglePoemLiked(false))
        }
    }

    fun onBookmarkClicked() = viewModelScope.launch {
        updateUi(PoemEvent.TogglePoemBookmarked(true))
        try {
            //TODO network call to like/unlike
            delay(2000)
        } catch (e: Exception) {
            updateUi(PoemEvent.TogglePoemBookmarked(false))
        }
    }

    private fun deleteFromRemote() = viewModelScope.launch {
        showLoading()
        try {
            //TODO network call to delete
            delay(2000)
            hideLoading()
            updateUi(PoemEvent.ShowSuccessSneaker("Poem Deleted Successfully!"))
            updateUi(PoemEvent.NavigateToBackstack)
        } catch (e: Exception) {
            hideLoading()
        }
    }

    private fun deleteFromLocal() = viewModelScope.launch {
        showLoading()
        try {
            //TODO database call to delete
            delay(2000)
            hideLoading()
            updateUi(PoemEvent.ShowSuccessSneaker("Poem Deleted Successfully!"))
            updateUi(PoemEvent.NavigateToBackstack)
        } catch (e: Exception) {
            hideLoading()
        }
    }

    private fun showLoading() = updateUi(PoemEvent.ShowLoadingDialog)

    private fun hideLoading() = updateUi(PoemEvent.HideLoadingDialog)

    private fun updateUi(event: PoemEvent) = viewModelScope.launch { _eventChannel.send(event) }

    sealed class PoemEvent {
        object ShowLoadingDialog : PoemEvent()
        object HideLoadingDialog : PoemEvent()
        object NavigateToBackstack : PoemEvent()
        object NavigateToArtistDetails : PoemEvent()
        object NavigateToComments : PoemEvent()
        object ShowPoemDeleteDialog : PoemEvent()
        object ClearCommentEditText : PoemEvent()
        data class TogglePoemLiked(val isLiked: Boolean) : PoemEvent()
        data class TogglePoemBookmarked(val isBookmarked: Boolean) : PoemEvent()
        data class ShowSnackBarError(val message: String) : PoemEvent()
        data class ShowSuccessSneaker(val message: String) : PoemEvent()
        data class NavigateToEditPoem(val poem: Poem) : PoemEvent()
    }
}