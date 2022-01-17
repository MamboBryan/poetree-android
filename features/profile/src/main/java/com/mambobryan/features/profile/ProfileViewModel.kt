package com.mambobryan.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mambo.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val preferences: UserPreferences
) : ViewModel() {

    private val _eventChannel = Channel<ProfileEvent>()
    val events = _eventChannel.receiveAsFlow()

    val mode = preferences.darkModeFlow.asLiveData()

    fun onAppThemeClicked() = updateUi(ProfileEvent.ShowAppThemeDialog)
    fun onAppThemeSelected(mode: Int) = viewModelScope.launch {
        preferences.updateDarkMode(mode)
    }
    fun onUpdateAccountClicked() = updateUi(ProfileEvent.NavigateToUpdateAccount)
    fun onUpdatePasswordClicked() = updateUi(ProfileEvent.NavigateToUpdatePassword)
    fun onPrivacyPolicyClicked() = updateUi(ProfileEvent.NavigateToPrivacyPolicy)
    fun onTermsAndConditionsClicked() = updateUi(ProfileEvent.NavigateToTermsAndConditions)

    private fun updateUi(event: ProfileEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class ProfileEvent{
        object ShowAppThemeDialog : ProfileEvent()
        object NavigateToUpdateAccount : ProfileEvent()
        object NavigateToUpdatePassword : ProfileEvent()
        object NavigateToPrivacyPolicy : ProfileEvent()
        object NavigateToTermsAndConditions : ProfileEvent()
    }

}