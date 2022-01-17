package com.mambo.core.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mambo.core.repository.PoemRepository
import com.mambo.core.utils.ConnectionLiveData
import com.mambo.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    poemRepository: PoemRepository,
    preferences: UserPreferences
) : AndroidViewModel(application) {

    init {
        runBlocking {
            isOnBoarded = preferences.isOnBoarded.first()
            isLoggedIn = preferences.isLoggedIn.first()
            isUserSetup = preferences.isUserSetup.first()
        }
    }

    private val _connection = ConnectionLiveData(application)
    val connection: ConnectionLiveData get() = _connection

    private val _eventChannel = Channel<MainEvent>()
    val events = _eventChannel.receiveAsFlow()

    val darkMode = preferences.darkModeFlow

    var isOnBoarded: Boolean
    var isLoggedIn: Boolean
    var isUserSetup: Boolean
    var backIsPressed = false

    val feeds = poemRepository.getLocalPoems("").cachedIn(viewModelScope)
//    val localPoems = poemRepository.getLocalPoems("").cachedIn(viewModelScope)

    private fun updateUi(event: MainEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class MainEvent {
        data class UpdateDarkMode(val mode: Int) : MainEvent()
    }

}