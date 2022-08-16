package com.mambobryan.features.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mambo.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardViewModel @Inject constructor(
    private val preferences: UserPreferences
) : ViewModel() {

    private val _onBoardEventChannel = Channel<OnboardEvent>()
    val onBoardEvent = _onBoardEventChannel.receiveAsFlow()

    fun onFinishOnBoardingClicked() = viewModelScope.launch {
        preferences.onBoarded()
        _onBoardEventChannel.send(OnboardEvent.NavigateToAuthentication)
    }

    sealed class OnboardEvent {
        object NavigateToAuthentication : OnboardEvent()
    }

}