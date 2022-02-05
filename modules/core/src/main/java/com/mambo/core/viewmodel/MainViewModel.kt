package com.mambo.core.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.mambo.core.repository.PoemRepository
import com.mambo.core.utils.ConnectionLiveData
import com.mambo.data.models.Poem
import com.mambo.data.models.Topic
import com.mambo.data.models.User
import com.mambo.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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

    var dataLoaded = false
    var isOnBoarded: Boolean
    var isLoggedIn: Boolean
    var isUserSetup: Boolean

    val darkModeFlow = preferences.darkMode

    val feedPoems = poemRepository.feedPoems().cachedIn(viewModelScope)
    val publicPoems = poemRepository.publishedPoems().cachedIn(viewModelScope)

    private val searchTopic = MutableStateFlow<Topic?>(null)
    private val searchQuery = MutableStateFlow("")
    private val _searches = combine(searchTopic, searchQuery) { topic, query ->
        Triple(topic, query, null)
    }.flatMapLatest { (topic, query, _) -> poemRepository.searchPoems(topic, query) }
    val searchedPoems = _searches.cachedIn(viewModelScope)

    private val bookmarksQuery = MutableStateFlow("")
    private val _bookmarks = bookmarksQuery.flatMapLatest { poemRepository.searchPoems(it) }
    val bookmarkedPoems = _bookmarks.cachedIn(viewModelScope)

    private val unpublishedQuery = MutableStateFlow("")
    private val _unpublished =
        unpublishedQuery.flatMapLatest { poemRepository.unpublishedPoems("1", it) }
    val unpublishedPoems = _unpublished.cachedIn(viewModelScope)

    private val _poem = state.getLiveData<Poem?>("poem", null)
    val poem: LiveData<Poem?> get() = _poem

    private val _topic = state.getLiveData<Topic?>("topic", null)
    val topic: LiveData<Topic?> get() = _topic

    private val _user = state.getLiveData<User?>("user", null)
    val user: LiveData<User?> get() = _user

    init {
        runBlocking {
            isOnBoarded = preferences.isOnBoarded.first()
            isLoggedIn = preferences.isLoggedIn.first()
            isUserSetup = preferences.isUserSetup.first()
        }
    }

    fun mockDataLoading(): Boolean {
        viewModelScope.launch {
            delay(1000)
            dataLoaded = true
        }
        return dataLoaded
    }

    fun isValidPoem(json: String?): Boolean {

        if (json.isNullOrEmpty()) return false

        return try {
            val poemUpdate = Gson().fromJson(json, Poem::class.java)
            setPoem(poemUpdate)
            poemUpdate != null
        } catch (e: Exception) {
            false
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
        unpublishedQuery.value = query
    }

    fun setupDailyInteractionReminder() = updateUi(MainEvent.SetupDailyInteractionReminder)

    private fun updateUi(event: MainEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class MainEvent {
        object SetupDailyInteractionReminder : MainEvent()
    }

}