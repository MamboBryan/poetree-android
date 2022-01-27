package com.mambo.core.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.mambo.core.repository.PoemRepository
import com.mambo.core.utils.ConnectionLiveData
import com.mambo.data.models.Poem
import com.mambo.data.models.Topic
import com.mambo.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    poemRepository: PoemRepository,
    preferences: UserPreferences,
    state: SavedStateHandle
) : AndroidViewModel(application) {

    private val _connection = ConnectionLiveData(application)
    val connection: ConnectionLiveData get() = _connection

    private val _eventChannel = Channel<MainEvent>()
    val events = _eventChannel.receiveAsFlow()

    val darkMode = preferences.darkMode

    var isOnBoarded: Boolean
    var isLoggedIn: Boolean
    var isUserSetup: Boolean
    var backIsPressed = false

    val feeds = poemRepository.getLocalPoems("").cachedIn(viewModelScope)

    private val searchQuery = MutableStateFlow("")
    private val searchesFlow = searchQuery.flatMapLatest { poemRepository.searchPoems(it) }
    val searches = searchesFlow.cachedIn(viewModelScope)

    fun onSearchQueryUpdated(text:String){
        searchQuery.value = text
    }

    val bookmarks = poemRepository.getLocalPoems("").cachedIn(viewModelScope)
    val unpublished = poemRepository.getLocalPoems("").cachedIn(viewModelScope)

    private val _poem = MutableLiveData<Poem?>(null)
    val poem: LiveData<Poem?> get() = _poem

    private val _topic = state.getLiveData<Topic?>("topic")
    val topic: LiveData<Topic?> get() = _topic

    init {
        runBlocking {
            isOnBoarded = preferences.isOnBoarded.first()
            isLoggedIn = preferences.isLoggedIn.first()
            isUserSetup = preferences.isUserSetup.first()
        }
    }

    fun setPoem(poem: Poem?) {
        _poem.value = poem
    }

    fun setTopic(topic: Topic?) {
        _topic.value = topic
    }

    private fun updateUi(event: MainEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class MainEvent {
        data class UpdateDarkMode(val mode: Int) : MainEvent()
    }

}