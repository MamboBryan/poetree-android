package com.mambo.compose

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mambo.core.repository.PoemRepository
import com.mambo.data.models.Poem
import com.mambo.data.models.Topic
import com.mambo.data.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ComposeViewModel @Inject constructor(
    private val repository: PoemRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    private val _eventChannel = Channel<ComposeEvent>()
    val events = _eventChannel.receiveAsFlow()

    var poem = state.get<Poem>("poem")
        set(value) {
            field = value
            state.set("poem", value)
        }

    var topic = state.get<Topic>("topic")
        set(value) {
            field = value
            state.set("topic", value)
        }

    var poemTitle = state.get<String>("poem_title") ?: ""
        set(value) {
            field = value
            state.set("poem_title", value)
        }

    var poemContent = state.get<String>("poem_content") ?: ""
        set(value) {
            field = value
            state.set("poem_content", value)
        }

    fun getHtml(): String {
        val html = StringBuilder()
        val prettyTime = PrettyTime()

        val topic = poem?.topic?.name ?: "Topicless"
        val duration = prettyTime.formatDuration(poem?.createdAt).ifBlank { "A minute" }
        val title = poemTitle.ifEmpty { "Untitled" }
        val content = poemContent.ifEmpty { "No art has been penned down" }

        html.append("$topic • $duration ago")
        html.append("<h2><b>$title</b></h2>")
        html.append("<i>$content</i>")
        html.append("<br><br>")

        return html.toString()
    }

    fun onEditClicked() {
        updateUi(ComposeEvent.NavigateToComposeView)
    }

    fun onPreviewClicked() {
        updateUi(ComposeEvent.NavigateToPreview)
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
            update(updatedPoem)

        } else {

            val newPoem = getNewPoem()
            save(newPoem)

        }
    }

    fun onPublish() {

        if (poemTitle.isBlank()) {
            showInvalidInputMessage("Title cannot be empty")
            return
        }

        if (poemContent.isBlank()) {
            showInvalidInputMessage("Content cannot be empty")
            return
        }

        if (poem != null) {

            val updatedPoem = poem!!.copy(title = poemTitle, content = poemContent)
            updateAndPublish(updatedPoem)

        } else {

            val newPoem = getNewPoem()
            saveAndPublish(newPoem)

        }
    }

    fun onBackClicked() {
        updateUi(ComposeEvent.NavigateToBackstack)
    }

    fun updatePoem(selectedPoem: Poem?) {
        if (selectedPoem != null) {
            poem = selectedPoem
            topic = selectedPoem.topic
            poemTitle = selectedPoem.title!!
            poemContent = selectedPoem.content!!
        }
    }

    private fun getUser() = User(
        "1",
        "MamboBryan",
        "https://images.unsplash.com/photo-1558945657-484aa38065ec?ixlib=rb-1.2.1&ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&auto=format&fit=crop&w=633&q=80",
        Date(),
        "This is my moment",
        "male"
    )

    private fun getNewPoem() = Poem(
        createdAt = Date(),
        updatedAt = Date(),
        user = getUser(),
        userId = getUser().id,
        title = poemTitle,
        content = poemContent,
        isOffline = true,
        isPublic = false
    )

    private fun save(poem: Poem) = viewModelScope.launch {

        repository.save(poem)
        updateUi(ComposeEvent.NavigateToBackstack)

    }

    private fun saveAndPublish(poem: Poem) = viewModelScope.launch {
        repository.save(poem)
        updateUi(ComposeEvent.NavigateToPublish(poem))
    }

    private fun update(poem: Poem) = viewModelScope.launch {

        repository.update(poem)
        updateUi(ComposeEvent.NavigateToBackstack)

    }

    private fun updateAndPublish(poem: Poem) = viewModelScope.launch {
        repository.update(poem)
        updateUi(ComposeEvent.NavigateToPublish(poem))
    }

    private fun showInvalidInputMessage(message: String) {
        updateUi(ComposeEvent.ShowInvalidInputMessage(message))
    }

    private fun updateUi(event: ComposeEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class ComposeEvent {
        data class ShowInvalidInputMessage(val message: String) : ComposeEvent()
        data class NavigateToPublish(val poem: Poem) : ComposeEvent()
        object NavigateToPreview : ComposeEvent()
        object NavigateToBackstack : ComposeEvent()
        object NavigateToComposeView : ComposeEvent()
    }

}