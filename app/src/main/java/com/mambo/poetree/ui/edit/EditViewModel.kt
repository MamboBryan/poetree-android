package com.mambo.poetree.ui.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mambo.poetree.data.local.PoemsDao
import com.mambo.poetree.data.model.Poem
import com.mambo.poetree.data.model.User
import com.mambo.poetree.utils.Result.RESULT_CREATE_OK
import com.mambo.poetree.utils.Result.RESULT_EDIT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
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

    fun onSaveForLaterClicked() {

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

    private fun stashPoem(poem: Poem) {
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
        object NavigateToPreview : EditPoemEvent()
        object NavigateToEditView : EditPoemEvent()
    }

}