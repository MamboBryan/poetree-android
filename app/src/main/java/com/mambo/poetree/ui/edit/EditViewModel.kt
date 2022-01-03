package com.mambo.poetree.ui.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mambo.data.Poem
import com.mambo.data.User
import com.mambo.local.PoemsDao
import com.mambo.poetree.utils.Result.RESULT_CREATE_OK
import com.mambo.poetree.utils.Result.RESULT_EDIT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val poemsDao: PoemsDao,
    private val state: SavedStateHandle
) : ViewModel() {

    val poem = state.get<Poem>("poem")

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

    private val _editPoemEventChannel = Channel<EditPoemEvent>()
    val editPoemEvent = _editPoemEventChannel.receiveAsFlow()

    private fun getUser() = User(
        "1",
        "MamboBryan",
        "https://images.unsplash.com/photo-1558945657-484aa38065ec?ixlib=rb-1.2.1&ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&auto=format&fit=crop&w=633&q=80"
    )

    fun onSave() {

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

            val newPoem = Poem(title = poemTitle, content = poemContent, user = getUser())
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
            updatePoem(updatedPoem)

        } else {

            val newPoem = Poem(title = poemTitle, content = poemContent, user = getUser())
            createPoem(newPoem)

        }
    }

    fun onPreviewClicked() {
        viewModelScope.launch {
            _editPoemEventChannel.send(EditPoemEvent.NavigateToPreview)
        }
    }

    fun onEditClicked() {
        viewModelScope.launch {
            _editPoemEventChannel.send(EditPoemEvent.NavigateToEditView)
        }
    }

    private fun saveNewPoem(poem: Poem) {
        viewModelScope.launch {

            _editPoemEventChannel.send(
                EditPoemEvent.NavigateToComposeFragment(poem)
            )
        }
    }

    private fun updateSavedPoem(poem: Poem) {
        viewModelScope.launch {

            poemsDao.update(poem)

            _editPoemEventChannel.send(
                EditPoemEvent.NavigateToComposeFragment(poem)
            )
        }
    }

    private fun showInvalidInputMessage(message: String) {
        viewModelScope.launch {
            _editPoemEventChannel.send(EditPoemEvent.ShowInvalidInputMessage(message))
        }
    }

    private fun updatePoem(poem: Poem) {
        viewModelScope.launch {

            poemsDao.update(poem)

            _editPoemEventChannel.send(
                EditPoemEvent.NavigateBackWithResult(
                    RESULT_EDIT_OK
                )
            )
        }
    }

    private fun createPoem(poem: Poem) {
        viewModelScope.launch {

            poemsDao.insert(poem)

            _editPoemEventChannel.send(
                EditPoemEvent.NavigateBackWithResult(
                    RESULT_CREATE_OK
                )
            )
        }
    }

    sealed class EditPoemEvent {
        data class ShowInvalidInputMessage(val message: String) : EditPoemEvent()
        data class NavigateBackWithResult(val result: Int) : EditPoemEvent()
        data class NavigateToComposeFragment(val poem: Poem) : EditPoemEvent()
        object NavigateToPreview : EditPoemEvent()
        object NavigateToEditView : EditPoemEvent()
    }

}