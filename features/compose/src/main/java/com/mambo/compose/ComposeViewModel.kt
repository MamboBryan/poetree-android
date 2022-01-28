package com.mambo.compose

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mambo.core.repository.PoemRepository
import com.mambo.data.models.Poem
import com.mambo.data.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ComposeViewModel @Inject constructor(
    private val repository: PoemRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    private val poem = state.get<Poem>("poem")

    var poemTitle = state.get<String>("poem_title") ?: poem?.title ?: ""
        set(value) {
            field = value
            state.set("poem_title", value)
        }

    var poemContent = state.get<String>("poem_content") ?: poem?.content ?: ""
        set(value) {
            field = value
            state.set("poem_content", value)
        }

    private val _eventChannel = Channel<ComposeEvent>()
    val events = _eventChannel.receiveAsFlow()

    private fun getUser() = User(
        "1",
        "MamboBryan",
        "https://images.unsplash.com/photo-1558945657-484aa38065ec?ixlib=rb-1.2.1&ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&auto=format&fit=crop&w=633&q=80"
    )

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

            val updatedPoem = poem.copy(title = poemTitle, content = poemContent)
            updateSavedPoem(updatedPoem)

        } else {

            val newPoem = getNewPoem()
            saveNewPoem(newPoem)

        }
    }

    fun onStash() {

        val today = Date()

        val dateFormat = SimpleDateFormat("EEE, MMM d, ''yy")
        val date = dateFormat.format(today)

        if (poemContent.isBlank()) {
            showInvalidInputMessage("Content cannot be empty")
            return
        }

        if (poemTitle.isBlank()) {
            poemTitle = "Untitled $date"
        }

        if (poem != null) {

            val updatedPoem = poem.copy(content = poemContent)
            update(updatedPoem)

        } else {

            val newPoem = getNewPoem()
            save(newPoem)

        }
    }

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

    fun onBackClicked() {
        updateUi(ComposeEvent.NavigateToBackstack)
    }

    fun onPreviewClicked() {
        updateUi(ComposeEvent.NavigateToPreview)
    }

    fun onEditClicked() {
        updateUi(ComposeEvent.NavigateToComposeView)
    }

    private fun saveNewPoem(poem: Poem) {
        updateUi(ComposeEvent.NavigateToPublish(poem))
    }

    private fun updateSavedPoem(poem: Poem) = viewModelScope.launch {

        repository.update(poem)
        updateUi(ComposeEvent.NavigateToPublish(poem))

    }

    private fun showInvalidInputMessage(message: String) {
        updateUi(ComposeEvent.ShowInvalidInputMessage(message))
    }

    private fun update(poem: Poem) = viewModelScope.launch {

        repository.update(poem)
        updateUi(ComposeEvent.NavigateToBackstack)

    }

    private fun save(poem: Poem) = viewModelScope.launch {

        repository.save(poem)
        updateUi(ComposeEvent.NavigateToBackstack)

    }

    private fun updateUi(event: ComposeEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class ComposeEvent {
        data class ShowInvalidInputMessage(val message: String) : ComposeEvent()
        data class NavigateBackWithResult(val result: Int) : ComposeEvent()
        data class NavigateToPublish(val poem: Poem) : ComposeEvent()
        object NavigateToPreview : ComposeEvent()
        object NavigateToBackstack : ComposeEvent()
        object NavigateToComposeView : ComposeEvent()
    }

}