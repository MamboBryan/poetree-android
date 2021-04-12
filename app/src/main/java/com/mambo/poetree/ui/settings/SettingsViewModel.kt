package com.mambo.poetree.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mambo.poetree.repositories.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _settingsEventChannel = Channel<SettingsEvent>()
    val settingsEvent = _settingsEventChannel.receiveAsFlow()

    private val darkModeFlow = preferencesRepository.darkModeFlow
    val isDarkModeEnabled = darkModeFlow.asLiveData()

    fun updateAppTheme(isChecked: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateDarkMode(isChecked)
            _settingsEventChannel.send(SettingsEvent.UpdateAppTheme(isChecked))
        }
    }

    sealed class SettingsEvent {
        data class UpdateAppTheme(val isDarkModeEnable: Boolean) : SettingsEvent()
    }
}