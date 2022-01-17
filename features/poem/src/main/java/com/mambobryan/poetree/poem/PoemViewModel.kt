package com.mambobryan.poetree.poem

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mambo.core.repository.PoemRepository
import com.mambo.data.models.Poem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PoemViewModel @Inject constructor(
    private val poemRepository: PoemRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    private val _eventChannel = Channel<PoemEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val _poem = state.getLiveData<Poem>("poem", null)
    val poem = _poem.value

    private val title = "The Emergence"
    private val content =
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
    private val topic = "Motivation"
    private val days = "2 days ago"

    fun getHtml(): String {
        val html = StringBuilder()

        html.append("<h2><i><b> $title </b></i></h2>")
        html.append("\n")
        html.append("<i><b>${topic}</b> • ${days}</i>")
        html.append("<br><br>")
        html.append("<i>${content}</i>")
        html.append("<br><br>")
        html.append("<i>")
        html.append("${200} likes")
        html.append(" • ")
        html.append("${200} comments")
        html.append(" • ")
        html.append("${200} bookmarks")
        html.append(" • ")
        html.append("${200} reads")
        html.append("</i>")

        return html.toString()
    }

    fun onCommentsClicked() = updateUi(PoemEvent.NavigateToComments)

    fun onArtistImageClicked() = updateUi(PoemEvent.NavigateToArtistDetails)

    private fun updateUi(event: PoemEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class PoemEvent {
        object NavigateToArtistDetails : PoemEvent()
        object NavigateToComments : PoemEvent()
        data class NavigateToEditPoem(val poem: Poem) : PoemEvent()
        data class ShowUndoDeletePoemMessage(val poem: Poem) : PoemEvent()
        data class ShowPoemConfirmationMessage(val msg: String) : PoemEvent()
    }
}