package com.mambobryan.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mambo.data.models.Poem
import com.mambo.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val preferences: UserPreferences
) : ViewModel() {

    init {
        runBlocking { mode = preferences.darkMode.first() }
    }

    private val _eventChannel = Channel<ProfileEvent>()
    val events = _eventChannel.receiveAsFlow()

    var mode: Int

    fun onAppThemeClicked() = updateUi(ProfileEvent.ShowDarkModeDialog)

    fun onDarkModeSelected(mode: Int) = viewModelScope.launch { preferences.updateDarkMode(mode) }

    fun onPoemClicked(poem:Poem) = updateUi(ProfileEvent.NavigateToPoem(poem))

    private fun updateUi(event: ProfileEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class ProfileEvent {
        object ShowDarkModeDialog : ProfileEvent()
        data class NavigateToPoem(val poem: Poem) : ProfileEvent()
    }

}