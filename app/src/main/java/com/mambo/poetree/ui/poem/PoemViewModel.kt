package com.mambo.poetree.ui.poem

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mambo.data.Poem
import com.mambo.local.PoemsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class PoemViewModel @Inject constructor(
    private val poemsDao: PoemsDao,
    private val state: SavedStateHandle
) : ViewModel() {

    private val _poemEventChannel = Channel<PoemEvent>()
    val poemEvent = _poemEventChannel.receiveAsFlow()

    val poem = state.get<Poem>("poem")

    sealed class PoemEvent {
        object NavigateToPoemDiscussion : PoemEvent()
        data class NavigateToEditPoem(val poem: Poem) : PoemEvent()
        data class ShowUndoDeletePoemMessage(val poem: Poem) : PoemEvent()
        data class ShowPoemConfirmationMessage(val msg: String) : PoemEvent()
    }
}