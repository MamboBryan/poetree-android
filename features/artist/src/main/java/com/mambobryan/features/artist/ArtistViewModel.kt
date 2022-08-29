package com.mambobryan.features.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mambo.core.repository.PoemRepository
import com.mambo.data.models.Poem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(
    poemsRepository: PoemRepository
) : ViewModel() {

    private val _eventChannel = Channel<ArtistEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val user = MutableStateFlow("")
    private val _poems = user.flatMapLatest { poemsRepository.searchPoems(it) }
    val poems = _poems.cachedIn(viewModelScope)

    fun onPoemClicked(poem: Poem){
        updateUi(ArtistEvent.UpdatePoemInSharedViewModel(poem))
        updateUi(ArtistEvent.NavigateToPoem)
    }

    private fun updateUi(event: ArtistEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class ArtistEvent {
        object NavigateToPoem : ArtistEvent()
        data class UpdatePoemInSharedViewModel(val poem: Poem) : ArtistEvent()
    }

}