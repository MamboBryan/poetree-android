package com.mambobryan.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mambo.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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

    val darkModeFlow = preferences.darkMode
    var mode: Int

    fun onAppThemeClicked() = updateUi(ProfileEvent.ShowDarkModeDialog)

    fun onDarkModeSelected(mode: Int) = viewModelScope.launch { preferences.updateDarkMode(mode) }

    fun onUpdateAccountClicked() = updateUi(ProfileEvent.NavigateToUpdateAccount)

    fun onUpdatePasswordClicked() = updateUi(ProfileEvent.NavigateToUpdatePassword)

    fun onPrivacyPolicyClicked() = updateUi(ProfileEvent.NavigateToPrivacyPolicy)

    fun onTermsAndConditionsClicked() = updateUi(ProfileEvent.NavigateToTermsAndConditions)

    fun onLogOutClicked() = updateUi(ProfileEvent.ShowLogOutDialog)

    fun onLogOutConfirmed() = viewModelScope.launch {
        updateUi(ProfileEvent.ShowLoadingDialog)
        try {
            preferences.signOut()
            delay(2500)
            updateUi(ProfileEvent.HideLoadingDialog)
            updateUi(ProfileEvent.NavigateToLanding)
        } catch (e: Exception) {
            updateUi(ProfileEvent.HideLoadingDialog)
        }
    }

    private fun updateUi(event: ProfileEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class ProfileEvent {
        object ShowLogOutDialog : ProfileEvent()
        object ShowLoadingDialog : ProfileEvent()
        object HideLoadingDialog : ProfileEvent()
        object ShowDarkModeDialog : ProfileEvent()

        object NavigateToLanding : ProfileEvent()
        object NavigateToPrivacyPolicy : ProfileEvent()
        object NavigateToUpdateAccount : ProfileEvent()
        object NavigateToUpdatePassword : ProfileEvent()
        object NavigateToTermsAndConditions : ProfileEvent()
    }

}