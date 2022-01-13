package com.mambo.core.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mambo.core.repository.PoemRepository
import com.mambo.core.repository.PreferencesRepository
import com.mambo.core.utils.ConnectionLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    poemRepository: PoemRepository,
    preferencesRepository: PreferencesRepository
) : AndroidViewModel(application) {

    init {
        Log.i("FEEDS", "initializing viewmodel")
    }

    private val _connection = ConnectionLiveData(application)
    val connection: ConnectionLiveData get() = _connection

    private val _eventChannel = Channel<MainEvent>()
    val events = _eventChannel.receiveAsFlow()

    val darkMode = preferencesRepository.darkModeFlow

    val isLoggedIn = false
    var backIsPressed = false

//    val feeds = poemRepository.getLocalPoems("").cachedIn(viewModelScope)
//    val localPoems = poemRepository.getLocalPoems("").cachedIn(viewModelScope)

    private fun updateUi(event: MainEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class MainEvent {
        data class UpdateDarkMode(val mode: Int) : MainEvent()
    }

}