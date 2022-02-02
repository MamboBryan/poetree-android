package com.mambo.core.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mambo.core.repository.PoemRepository
import com.mambo.core.utils.ConnectionLiveData
import com.mambo.data.models.Poem
import com.mambo.data.models.Topic
import com.mambo.data.models.User
import com.mambo.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
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

    var darkMode: Int
    var isOnBoarded: Boolean
    var isLoggedIn: Boolean
    var isUserSetup: Boolean

    val feeds = poemRepository.getLocalPoems("").cachedIn(viewModelScope)

    private val searchTopic = MutableStateFlow<Topic?>(null)
    private val searchQuery = MutableStateFlow("")
    private val _searches = combine(searchTopic, searchQuery) { topic, query ->
        Triple(topic, query, null)
    }.flatMapLatest { (topic, query, _) -> poemRepository.searchPoems(topic, query) }
    val searches = _searches.cachedIn(viewModelScope)

    private val bookmarksQuery = MutableStateFlow("")
    private val _bookmarks = bookmarksQuery.flatMapLatest { poemRepository.searchPoems(it) }
    val bookmarks = _bookmarks.cachedIn(viewModelScope)

    private val libraryQuery = MutableStateFlow("")
    private val _publicPoems = libraryQuery.flatMapLatest { poemRepository.searchPoems(it) }
    val publicPoems = _publicPoems.cachedIn(viewModelScope)
    private val _privatePoems = libraryQuery.flatMapLatest { poemRepository.searchPoems(it) }
    val privatePoems = _privatePoems.cachedIn(viewModelScope)

    private val _poem = state.getLiveData<Poem?>("poem", null)
    val poem: LiveData<Poem?> get() = _poem

    private val _topic = state.getLiveData<Topic?>("topic", null)
    val topic: LiveData<Topic?> get() = _topic

    private val _user = state.getLiveData<User?>("user", null)
    val user: LiveData<User?> get() = _user

    init {
        runBlocking {
            darkMode = preferences.darkMode.first()
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

    fun setUser(user: User?) {
        _user.value = user
    }

    fun onSearchQueryUpdated(text: String) {
        searchQuery.value = text
    }

    fun updateBookmarkQuery(query: String) {
        bookmarksQuery.value = query
    }

    fun updateLibraryQuery(query: String) {
        libraryQuery.value = query
    }

    fun setupDailyInteractionReminder() = updateUi(MainEvent.SetupDailyInteractionReminder)

    private fun updateUi(event: MainEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class MainEvent {
        object SetupDailyInteractionReminder : MainEvent()
    }

}