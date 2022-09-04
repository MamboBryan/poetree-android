package com.mambo.compose

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mambo.core.repository.PoemRepository
import com.mambo.core.utils.toDate
import com.mambo.core.utils.toDateTimeString
import com.mambo.data.models.LocalPoem
import com.mambo.data.models.Poem
import com.mambo.data.models.Topic
import com.mambo.data.preferences.UserPreferences
import com.mambo.data.requests.CreatePoemRequest
import com.mambo.data.requests.EditPoemRequest
import com.mambo.data.responses.UserDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ComposeViewModel @Inject constructor(
    private val repository: PoemRepository,
    private val state: SavedStateHandle,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _eventChannel = Channel<ComposeEvent>()
    val events = _eventChannel.receiveAsFlow()

    var poem = state.get<Poem>("poem")
        set(value) {
            field = value
            state["poem"] = value
        }

    var topic = state.get<Topic>("topic")
        set(value) {
            field = value
            state["topic"] = value
        }

    var poemTitle = state.get<String>("poem_title") ?: ""
        set(value) {
            field = value
            state["poem_title"] = value
        }

    var poemContent = state.get<String>("poem_content") ?: ""
        set(value) {
            field = value
            state["poem_content"] = value
        }

    var poemHtml = state.get<String>("poem_html") ?: ""
        set(value) {
            field = value
            state["poem_html"] = value
        }

    private val _user = MutableLiveData<UserDetails>()

    init {
        getUserDetails()
    }

    private fun getUserDetails() {
        viewModelScope.launch {
            _user.value = userPreferences.user.firstOrNull()
        }
    }

    fun getHtml(): String {
        val html = StringBuilder()
        val prettyTime = PrettyTime()

        val topic = poem?.topic?.name ?: "Topicless"
        val duration = prettyTime.formatDuration(poem?.createdAt.toDate()).ifBlank { "A minute" }
        val title = poemTitle.ifEmpty { "Untitled" }
        val content = poemContent.ifEmpty { "No art has been penned down" }

        html.append("$topic • $duration ago")
        html.append("<h2><b>$title</b></h2>")
        html.append("<i>$content</i>")
        html.append("<br><br>")

        return html.toString()
    }

    fun onStash() {

        if (poemContent.isBlank()) {
            showInvalidInputMessage("Content cannot be empty")
            return
        }

        if (poemTitle.isBlank()) {
            val dateFormat = SimpleDateFormat("EEE, MMM d, ''yy")
            val date = dateFormat.format(Date())
            poemTitle = "Untitled $date"
        }

        if (poem != null) {

            val updatedPoem = poem!!.copy(title = poemTitle, content = poemContent)
            updateLocal(updatedPoem)

        } else {

            val newPoem = getNewPoem()
            createLocal(newPoem)

        }
    }

    fun onPublishConfirmed() {

        if (poemTitle.isBlank()) {
            showInvalidInputMessage("Title cannot be empty")
            return
        }

        if (poemContent.isBlank()) {
            showInvalidInputMessage("Content cannot be empty")
            return
        }

        if (poem?.topic == null) {
            showInvalidInputMessage("Select Topic to publish")
            return
        }

//        if (poem != null) {
//
//            val updatedPoem = poem!!.copy(title = poemTitle, content = poemContent)
//            updatePublished(updatedPoem)
//
//        } else {

        val newPoem = getNewPoem()
        createPublished(newPoem)

//        }
    }

    fun onDeleteConfirmed() {
        poem?.let { deleteLocal(it) }
    }

    fun onBackClicked() {
        updateUi(ComposeEvent.NavigateToBackstack)
    }

    private fun showInvalidInputMessage(message: String) {
        updateUi(ComposeEvent.ShowInvalidInputMessage(message))
    }

    private fun updateUi(event: ComposeEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    fun updatePoem(selectedPoem: Poem?) {

        if (selectedPoem != null) {
            poem = selectedPoem
            topic = selectedPoem.topic
            poemTitle = selectedPoem.title
            poemContent = selectedPoem.content
        }

    }

    private fun getNewPoem() = LocalPoem(
        createdAt = Date().toDateTimeString() ?: "",
        updatedAt = Date().toDateTimeString() ?: "",
        title = poemTitle,
        content = poemContent,
        html = poemContent,
        topic = topic
    )

    @Throws(NullPointerException::class)
    private fun getUpdatedPoem() = poem!!.copy(
        title = poemTitle,
        content = poemContent,
        html = poemContent,
        topic = topic
    )

    private fun createLocal(poem: Poem) = viewModelScope.launch {
        repository.saveLocal(poem.toLocalPoem())
    }

    private fun updateLocal(poem: Poem) = viewModelScope.launch {
        repository.saveLocal(poem.toLocalPoem())
    }

    private fun deleteLocal(poem: Poem) = viewModelScope.launch {
        repository.deleteLocal(poem.toLocalPoem())
        updateUi(ComposeEvent.ShowSuccess("Poem Deleted Successfully!"))
        updateUi(ComposeEvent.NavigateToBackstack)
    }

    private fun createPublished(poem: Poem) = viewModelScope.launch {

        updateUi(ComposeEvent.ShowLoading)

        val request = CreatePoemRequest(
            title = poem.title,
            content = poem.content,
            html = poem.html,
            topic = poem.topic!!.id
        )

        val response = repository.createPublished(request)

        if (response.isSuccessful.not()) {
            updateUi(ComposeEvent.HideLoading)
            updateUi(ComposeEvent.ShowError(response.message))
            return@launch
        }

        repository.deleteLocal(poem.toLocalPoem())

        val publishedPoem = response.data!!.toPoemDto()

        updateUi(ComposeEvent.HideLoading)
        updateUi(ComposeEvent.ShowSuccess(response.message))
        updateUi(ComposeEvent.NavigateToPoem(publishedPoem))

    }

    private fun updatePublished(poem: Poem) = viewModelScope.launch {

        updateUi(ComposeEvent.ShowLoading)

        val request = EditPoemRequest(
            poemId = poem.id,
            title = poem.title,
            content = poem.content,
            html = poem.html,
            topic = poem.topic!!.id
        )

        val response = repository.updatePublished(request)

        if (response.isSuccessful.not()) {
            updateUi(ComposeEvent.HideLoading)
            updateUi(ComposeEvent.ShowError(response.message))
            return@launch
        }

        repository.deleteLocal(poem.toLocalPoem())

        val publishedPoem = response.data!!.toPoemDto()

        updateUi(ComposeEvent.HideLoading)
        updateUi(ComposeEvent.ShowSuccess(response.message))
        updateUi(ComposeEvent.NavigateToPoem(publishedPoem))

    }

    sealed class ComposeEvent {
        data class ShowInvalidInputMessage(val message: String) : ComposeEvent()
        data class NavigateToPublish(val poem: Poem) : ComposeEvent()
        data class NavigateToPoem(val poem: Poem) : ComposeEvent()
        object NavigateToBackstack : ComposeEvent()
        object ShowLoading : ComposeEvent()
        object HideLoading : ComposeEvent()
        data class ShowSuccess(val message: String) : ComposeEvent()
        data class ShowError(val message: String) : ComposeEvent()
    }

}