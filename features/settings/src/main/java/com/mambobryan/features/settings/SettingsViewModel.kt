package com.mambobryan.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mambo.core.repository.PoemRepository
import com.mambo.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: UserPreferences
) : ViewModel() {

    @Inject
    lateinit var repository: PoemRepository

    private val _eventChannel = Channel<SettingsEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onUpdateAccountClicked() = updateUi(SettingsEvent.NavigateToUpdateAccount)

    fun onUpdatePasswordClicked() = updateUi(SettingsEvent.NavigateToUpdatePassword)

    fun onPrivacyPolicyClicked() = updateUi(SettingsEvent.NavigateToPrivacyPolicy)

    fun onTermsAndConditionsClicked() = updateUi(SettingsEvent.NavigateToTermsAndConditions)

    fun onLogOutClicked() = updateUi(SettingsEvent.ShowLogOutDialog)

    fun onDeleteAccountClicked() = updateUi(SettingsEvent.ShowDeleteAccountDialog)

    fun onDeleteAccountConfirmed() = viewModelScope.launch {
        updateUi(SettingsEvent.ShowLoadingDialog)
        try {
            preferences.signOut()
            delay(2500)
            updateUi(SettingsEvent.HideLoadingDialog)
            updateUi(SettingsEvent.NavigateToLanding)
        } catch (e: Exception) {
            updateUi(SettingsEvent.HideLoadingDialog)
        }
    }

    fun onLogOutConfirmed() = viewModelScope.launch {
        updateUi(SettingsEvent.ShowLoadingDialog)
        try {
            preferences.signOut()
            repository.deleteAllBookmarks()
            repository.deleteAllLocal()
            updateUi(SettingsEvent.HideLoadingDialog)
            updateUi(SettingsEvent.NavigateToLanding)
        } catch (e: Exception) {
            updateUi(SettingsEvent.HideLoadingDialog)
        }
    }

    private fun updateUi(event: SettingsEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class SettingsEvent {
        object ShowLogOutDialog : SettingsEvent()
        object ShowLoadingDialog : SettingsEvent()
        object HideLoadingDialog : SettingsEvent()
        object ShowDeleteAccountDialog : SettingsEvent()

        object NavigateToLanding : SettingsEvent()
        object NavigateToPrivacyPolicy : SettingsEvent()
        object NavigateToUpdateAccount : SettingsEvent()
        object NavigateToUpdatePassword : SettingsEvent()
        object NavigateToTermsAndConditions : SettingsEvent()
    }

}