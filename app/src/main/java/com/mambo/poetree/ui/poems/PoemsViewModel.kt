package com.mambo.poetree.ui.poems

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mambo.data.Poem
import com.mambo.local.PoemsDao
import com.mambo.local.SortOrder
import com.mambo.local.TopicsDao
import com.mambo.poetree.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PoemsViewModel @Inject constructor(
    private val poemsDao: PoemsDao,
    topicsDao: TopicsDao
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
    private val isPublished = MutableStateFlow(false)

    private val _poems = query.flatMapLatest { poemsDao.getPoems(it) }
    val poems = _poems.asLiveData()

    private val _topics = topicsDao.getAll()
    val topics = _topics.asLiveData()

    private val _poemEventChannel = Channel<PoemsEvent>()
    val poemsEvent = _poemEventChannel.receiveAsFlow()

    fun onCreateOrUpdateResult(result: Int) {
        when (result) {
            Result.RESULT_EDIT_OK -> showTaskConfirmationMessage("Task Updated")
            Result.RESULT_CREATE_OK -> showTaskConfirmationMessage("Task Created")
        }
    }

    private fun showTaskConfirmationMessage(message: String) {
        viewModelScope.launch {
            _poemEventChannel.send(PoemsEvent.ShowPoemConfirmationMessage(message))
        }
    }

    fun onPoemSelected(poem: Poem) = viewModelScope.launch {
        _poemEventChannel.send(PoemsEvent.NavigateToEditPoem(poem))
    }

    fun onCreatePoemClicked() = viewModelScope.launch {
        _poemEventChannel.send(PoemsEvent.NavigateToCreatePoem)
    }

    fun onPoemSwiped(poem: Poem) = viewModelScope.launch {
        poemsDao.delete(poem)
        _poemEventChannel.send(PoemsEvent.ShowUndoDeletePoemMessage(poem))
    }

    fun onPoemDeleteUndone(poem: Poem) = viewModelScope.launch {
        poemsDao.insert(poem)
    }

    sealed class PoemsEvent {
        object NavigateToCreatePoem : PoemsEvent()
        data class NavigateToEditPoem(val poem: Poem) : PoemsEvent()
        data class ShowUndoDeletePoemMessage(val poem: Poem) : PoemsEvent()
        data class ShowPoemConfirmationMessage(val msg: String) : PoemsEvent()
    }
}