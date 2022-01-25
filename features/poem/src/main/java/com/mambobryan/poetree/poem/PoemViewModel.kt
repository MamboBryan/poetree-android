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
    val poem = _poem.value

    private val _comment = MutableLiveData("")
    val comment: LiveData<String> get() = _comment

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

        html.append("<h2><i><b> $title </b></i></h2>")
        html.append("\n")
        html.append("<i><b>${topic}</b> • ${duration}</i>")
        html.append("<br><br>")
        html.append("<i>${content}</i>")
        html.append("<br><br>")

        if (isOnline) {
            html.append("<i>")
            html.append("${poem?.likesCount} likes")
            html.append(" • ")
            html.append("${poem?.commentsCount} comments")
            html.append(" • ")
            html.append("${poem?.bookmarksCount} bookmarks")
            html.append(" • ")
            html.append("${poem?.readsCount} reads")
            html.append("</i>")
        }

        return html.toString()
    }

    fun onCommentUpdated(text: String) {
        _comment.value = text
    }

    fun onCommentSendClicked() = viewModelScope.launch {
        showLoading()
        try {
            delay(2500)
            hideLoading()
        } catch (e: Exception) {
            hideLoading()
            updateUi(PoemEvent.ShowSnackBarError("Failed Sending Comment"))
        }
    }

    fun onCommentsClicked() = updateUi(PoemEvent.NavigateToComments)

    fun onArtistImageClicked() = updateUi(PoemEvent.NavigateToArtistDetails)

    fun onEditClicked() = updateUi(PoemEvent.NavigateToEditPoem(_poem.value!!))

    fun onDeleteClicked() = updateUi(PoemEvent.ShowPoemDeleteDialog)

    fun onDeleteConfirmed() = viewModelScope.launch {
        showLoading()
        try {
            delay(4000)
            hideLoading()
            updateUi(PoemEvent.ShowSuccessSneaker("Poem Deleted Successfully!"))
            updateUi(PoemEvent.NavigateToBackstack)
        } catch (e: Exception) {
            hideLoading()
        }
    }

    fun onPublishClicked() = viewModelScope.launch {
        showLoading()
        try {
            delay(4000)
            hideLoading()
        } catch (e: Exception) {
            hideLoading()
        }
    }

    fun onUnPublishClicked() = viewModelScope.launch {
        showLoading()
        try {
            delay(4000)
            hideLoading()
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
        data class ShowSnackBarError(val message: String) : PoemEvent()
        data class ShowSuccessSneaker(val message: String) : PoemEvent()
        data class NavigateToEditPoem(val poem: Poem) : PoemEvent()
    }
}