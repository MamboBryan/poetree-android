package com.mambobryan.poetree.poem

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mambo.core.repository.PoemRepository
import com.mambo.data.Poem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class PoemViewModel @Inject constructor(
    private val poemRepository: PoemRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    private val _poemEventChannel = Channel<PoemEvent>()
    val poemEvent = _poemEventChannel.receiveAsFlow()

    val poem = state.get<Poem>("poem")

    val title = "The Emergence"
    val content =
        """
                            Hidden in the waves <br>
                            Blossoming forth <br>
                            <br>
                            The way the pen behaves <br>
                            Like a cooking pot filled with broth <br>
                            <br>
                            The gem concealed in caves <br>
                            Emerging slowly like a sloth <br>
                            <br>
                            This is one of my faves <br>
                            Uncover the veil and remove the cloth <br>
                     
                        """
    val topic = "Motivation"
    val days = "2 days ago"

    sealed class PoemEvent {
        object NavigateToPoemDiscussion : PoemEvent()
        data class NavigateToEditPoem(val poem: Poem) : PoemEvent()
        data class ShowUndoDeletePoemMessage(val poem: Poem) : PoemEvent()
        data class ShowPoemConfirmationMessage(val msg: String) : PoemEvent()
    }
}