package com.mambobryan.poetree.poem

import androidx.lifecycle.*
import com.mambo.core.repository.CommentRepository
import com.mambo.core.repository.LikeRepository
import com.mambo.core.repository.PoemRepository
import com.mambo.core.utils.prettyCount
import com.mambo.core.utils.toDate
import com.mambo.data.models.Poem
import com.mambo.data.preferences.UserPreferences
import com.mambo.data.requests.CreateCommentRequest
import com.mambo.data.requests.CreatePoemRequest
import com.mambo.data.requests.EditPoemRequest
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

    @Inject
    lateinit var likeRepository: LikeRepository

    private val _eventChannel = Channel<PoemEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val _poem = state.getLiveData<Poem>("poem")
    val poem: LiveData<Poem> get() = _poem

    private val _comment = MutableLiveData("")
    val comment: LiveData<String> get() = _comment

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _reads = MutableStateFlow<Pair<Boolean, Long>?>(null)
    val reads: StateFlow<Pair<Boolean, Long>?> get() = _reads

    private val _likes = MutableStateFlow<Pair<Boolean, Long>?>(null)
    val likes: StateFlow<Pair<Boolean, Long>?> get() = _likes

    private val _bookmarks = MutableStateFlow<Pair<Boolean, Long>?>(null)
    val bookmarks: StateFlow<Pair<Boolean, Long>?> get() = _bookmarks

    private val _comments = MutableStateFlow<Pair<Boolean, Long>?>(null)
    val comments: StateFlow<Pair<Boolean, Long>?> get() = _comments

    var userDetails: UserDetails? = null
    var userId: String? = null

    init {
        viewModelScope.launch {
            userDetails = preferences.user.firstOrNull().also {
                userId = it?.id
            }

            _poem.value?.let {
                updatePoem(it)

            }

        }
    }

    private fun updatePoem(poem: Poem) {
        _reads.value = Pair(poem.read, poem.reads)
        _likes.value = Pair(poem.liked, poem.likes)
        _bookmarks.value = Pair(poem.bookmarked, poem.bookmarks)
        _comments.value = Pair(poem.commented, poem.comments)
    }

    fun getHtml(): String {
        val html = StringBuilder()
        val prettyTime = PrettyTime()

        val topic = (_poem.value?.topic?.name ?: "Topicless").replaceFirstChar { it.uppercase() }
        val duration = prettyTime.formatDuration(_poem.value?.createdAt.toDate())
        val reads = "${prettyCount(_reads.value?.second ?: 0)} reads"

        html.append("$topic • $duration • $reads")
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

            updateUi(PoemEvent.SneakSuccess(response.message))
            updateUi(PoemEvent.ClearCommentEditText)
        } catch (e: Exception) {
            _isLoading.value = false
            updateUi(PoemEvent.ShowSnackBarError("Failed Sending Comment"))
        }
    }

    fun onDeleteConfirmed() {

        val isRemote = _poem.value?.isLocal()?.not() ?: false

        if (isRemote) deleteFromRemote()
        else deleteFromLocal()

    }

    fun onPublishConfirmed() = viewModelScope.launch {

        val poem = _poem.value!!

        when (poem.remoteId != null) {
            true -> updatePublished(poem)
            false -> createPublished(poem)
        }

    }

    private fun createPublished(poem: Poem) = viewModelScope.launch {

        showLoading()

        val request = CreatePoemRequest(
            title = poem.title,
            content = poem.content,
            html = poem.html,
            topic = poem.topic!!.id
        )

        val response = poemRepository.createPublished(request)

        if (response.isSuccessful.not()) {
            hideLoading()
            updateUi(PoemEvent.ShowError(response.message))
            return@launch
        }

        poemRepository.deleteLocal(poem.toLocalPoem())

        val publishedPoem = response.data!!.toPoemDto()

        hideLoading()
        updateUi(PoemEvent.ShowSuccess(response.message))
        updatePoem(publishedPoem)
        _poem.value = publishedPoem

    }

    private fun updatePublished(poem: Poem) = viewModelScope.launch {

        showLoading()

        val request = EditPoemRequest(
            poemId = poem.id,
            title = poem.title,
            content = poem.content,
            html = poem.html,
            topic = poem.topic!!.id
        )

        val response = poemRepository.updatePublished(request)

        if (response.isSuccessful.not()) {
            hideLoading()
            updateUi(PoemEvent.ShowError(response.message))
            return@launch
        }

        poemRepository.deleteLocal(poem.toLocalPoem())

        val publishedPoem = response.data!!.toPoemDto()

        hideLoading()
        updateUi(PoemEvent.ShowSuccess(response.message))
        updatePoem(publishedPoem)
        _poem.value = publishedPoem

    }

    fun onPoemRead() = viewModelScope.launch {

        try {

            updateReads(read = true)

            val response = poemRepository.markAsRead(poemId = poem.value!!.id)

            if (response.isSuccessful.not()) {
                updateUi(PoemEvent.ShowSnackBarError(response.message))
                updateReads()
                return@launch
            }

            updateUi(PoemEvent.SneakSuccess(response.message))

        } catch (e: Exception) {
            updateUi(PoemEvent.ShowError(e.localizedMessage ?: "Error Marking Poem as read"))
            updateReads()
        }
    }

    private fun updateReads(read: Boolean = false) {
        _reads.value = _reads.value?.let { (_, reads) ->
            Pair(read, if (read) reads + 1 else reads - 1)
        }
    }

    fun onLikeClicked() = viewModelScope.launch {

        val liked = poem.value?.liked ?: false

        try {

            updateLikes(liked.not())

            val response = when (liked) {
                true -> likeRepository.unLikePoem(poemId = poem.value!!.id)
                false -> likeRepository.likePoem(poemId = poem.value!!.id)
            }

            if (response.isSuccessful.not()) {
                updateUi(PoemEvent.ShowSnackBarError(response.message))
                updateLikes(liked)
                return@launch
            }

            updateUi(PoemEvent.SneakSuccess(response.message))

        } catch (e: Exception) {
            updateUi(PoemEvent.ShowError(e.localizedMessage ?: "Error Liking Poem"))
            updateLikes(liked)
        }
    }

    private fun updateLikes(liked: Boolean) {
        _likes.value = _likes.value?.let { (_, likes) ->
            Pair(liked, if (liked) likes + 1 else likes - 1)
        }
    }

    fun onBookmarkClicked() = viewModelScope.launch {

        val bookmarked = poem.value?.bookmarked ?: false

        try {

            updateBookmarked(bookmarked.not())

            val response = when (bookmarked) {
                true -> poemRepository.unBookmark(poemId = poem.value!!.id)
                false -> poemRepository.bookmark(poemId = poem.value!!.id)
            }

            if (response.isSuccessful.not()) {
                updateUi(PoemEvent.ShowSnackBarError(response.message))
                updateBookmarked(bookmarked)
                return@launch
            }

            val bookmark = _poem.value!!.toBookmark()

            when (bookmarked.not()) {
                true -> poemRepository.saveBookmark(bookmark)
                false -> poemRepository.deleteBookmark(bookmark)
            }

            updateUi(PoemEvent.SneakSuccess(response.message))

        } catch (e: Exception) {
            updateUi(PoemEvent.ShowError(e.localizedMessage ?: "Error Bookmarking Poem"))
            updateBookmarked(bookmarked)
        }
    }

    private fun updateBookmarked(bookmarked: Boolean) {
        _bookmarks.value = _bookmarks.value?.let { (_, bookmarks) ->
            Pair(bookmarked, if (bookmarked) bookmarks + 1 else bookmarks - 1)
        }
    }

    private fun deleteFromRemote() = viewModelScope.launch {
        showLoading()
        try {
            //TODO network call to delete
            delay(2000)
            hideLoading()
            updateUi(PoemEvent.SneakSuccess("Poem Deleted Successfully!"))
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
            updateUi(PoemEvent.SneakSuccess("Poem Deleted Successfully!"))
            updateUi(PoemEvent.NavigateToBackstack)
        } catch (e: Exception) {
            hideLoading()
        }
    }

    private fun showLoading() = updateUi(PoemEvent.ShowLoadingDialog)

    private fun hideLoading() = updateUi(PoemEvent.HideLoadingDialog)

    private fun updateUi(event: PoemEvent) = viewModelScope.launch { _eventChannel.send(event) }

    fun getPoemUpdate() = viewModelScope.launch {

        val response = poemRepository.getPublished(poemId = poem.value!!.id)

        if (response.isSuccessful.not()) {
            updateUi(PoemEvent.SneakError("Failed getting poem updates"))
            return@launch
        }

        val data = response.data

        val updatedPoem = data?.toPoemDto()
        _poem.value = updatedPoem
        updatedPoem?.toBookmark()?.let { poemRepository.updateBookmark(it) }
        updateUi(PoemEvent.SneakSuccess("Got poem updates"))

    }

    fun saveLocalBookmark() = viewModelScope.launch {
        poemRepository.saveBookmark(_poem.value!!.toBookmark())
    }

    sealed class PoemEvent {
        object ShowLoadingDialog : PoemEvent()
        object HideLoadingDialog : PoemEvent()
        object NavigateToBackstack : PoemEvent()
        object ClearCommentEditText : PoemEvent()
        data class ShowSnackBarError(val message: String) : PoemEvent()
        data class SneakSuccess(val message: String) : PoemEvent()
        data class ShowSuccess(val message: String) : PoemEvent()
        data class SneakError(val message: String) : PoemEvent()
        data class ShowError(val message: String) : PoemEvent()
    }
}