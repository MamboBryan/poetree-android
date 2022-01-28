package com.mambobryan.poetree.poem

import androidx.lifecycle.*
import com.mambo.core.repository.PoemRepository
import com.mambo.data.models.Poem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.ocpsoft.prettytime.PrettyTime
import javax.inject.Inject

@HiltViewModel
class PoemViewModel @Inject constructor(
    private val poemRepository: PoemRepository,
    state: SavedStateHandle
) : ViewModel() {

    private val _eventChannel = Channel<PoemEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val _poem = state.getLiveData<Poem>("poem", null)
    val poem: LiveData<Poem> get() = _poem

    private val _comment = MutableLiveData("")
    val comment: LiveData<String> get() = _comment

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading


    private var title = ""
    private var content = ""
    private var topic = ""
    private var duration = ""
    var isOnline = false
    var isUser = false

    fun updatePoem(poem: Poem) {
        _poem.value = poem

        val prettyTime = PrettyTime()

        title = poem.title!!
        content = poem.content!!

        topic = poem.topic?.name ?: "No Topic"
        duration = prettyTime.formatDuration(poem.createdAt)
        isOnline = poem.isOffline?.not() ?: false

    }

    fun getHtml(): String {
        val html = StringBuilder()
        val prettyTime = PrettyTime()

        val topic = _poem.value?.topic?.name ?: "Topicless"
        val duration = prettyTime.formatDuration(_poem.value?.createdAt)

        html.append("$topic • $duration")
        html.append("<h2><b> ${_poem.value?.title}</b></h2>")
        html.append("By • ${_poem.value?.user?.username}")
        html.append("<br><br>")
        html.append("<i>${_poem.value?.content}</i>")
        html.append("<br><br>")

        if (isOnline) {
            html.append("")
            html.append("${_poem.value?.likesCount} likes")
            html.append(" • ")
            html.append("${_poem.value?.commentsCount} comments")
            html.append(" • ")
            html.append("${_poem.value?.bookmarksCount} bookmarks")
            html.append(" • ")
            html.append("${_poem.value?.readsCount} reads")
            html.append("")
        }

        return html.toString()
    }

    fun onCommentUpdated(text: String) {
        _comment.value = text
    }

    fun onCommentSendClicked() = viewModelScope.launch {
        _isLoading.value = true
        try {
            delay(2500)
            _isLoading.value = false
            _comment.value = ""
            updateUi(PoemEvent.ClearCommentEditText)
        } catch (e: Exception) {
            _isLoading.value = false
            updateUi(PoemEvent.ShowSnackBarError("Failed Sending Comment"))
        }
    }

    fun onCommentsClicked() = updateUi(PoemEvent.NavigateToComments)

    fun onArtistImageClicked() = updateUi(PoemEvent.NavigateToArtistDetails)

    fun onEditClicked() = updateUi(PoemEvent.NavigateToEditPoem(_poem.value!!))

    fun onDeleteClicked() = updateUi(PoemEvent.ShowPoemDeleteDialog)

    fun onDeleteConfirmed() {

        val isRemote = _poem.value?.isPublic ?: false

        if (isRemote)
            deleteFromRemote()
        else
            deleteFromLocal()
    }

    fun onPublishClicked() = viewModelScope.launch {
        showLoading()
        try {
            delay(2000)
            hideLoading()
        } catch (e: Exception) {
            hideLoading()
        }
    }

    fun onUnPublishClicked() = viewModelScope.launch {
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

    private fun updateUi(event: PoemEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

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