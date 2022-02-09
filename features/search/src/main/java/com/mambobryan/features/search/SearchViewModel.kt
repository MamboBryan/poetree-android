package com.mambobryan.features.search

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
class SearchViewModel @Inject constructor(
    poemsRepository: PoemRepository
): ViewModel() {

    private val _eventChannel = Channel<SearchEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onPoemClicked(poem: Poem){
        updateUi(SearchEvent.UpdatePoemInSharedViewModel(poem))
        updateUi(SearchEvent.NavigateToPoem)
    }

    private fun updateUi(event: SearchEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class SearchEvent{
        data class UpdatePoemInSharedViewModel(val poem: Poem) :SearchEvent()
        object NavigateToPoem : SearchEvent()
    }

}