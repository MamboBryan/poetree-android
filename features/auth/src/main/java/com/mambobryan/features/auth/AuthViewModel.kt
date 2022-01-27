package com.mambobryan.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mambo.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val preferences: UserPreferences
) : ViewModel() {

    private val _eventChannel = Channel<AuthEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onSignInClicked(email: String, password: String) = viewModelScope.launch {
        showLoading()
        try {
            //TODO network sign in call
            delay(2000)
//            preferences.loggedIn("")
            hideLoading()
            showSuccess("Logged In Successfully")
//            updateUi(AuthEvent.NavigateToHome)
        } catch (e: Exception) {
            hideLoading()
            showError(e.localizedMessage!!)
        }
    }

    fun onCreateClicked() = updateUi(AuthEvent.NavigateToSignUp)

    fun onSignUpClicked(email: String, password: String) {
        updateUi(AuthEvent.NavigateToSetup)
    }

    fun onSignUpLoginClicked() = updateUi(AuthEvent.NavigateToSignIn)

    private fun showError(message: String) = updateUi(AuthEvent.ShowErrorMessage(message))
    private fun showSuccess(message: String) = updateUi(AuthEvent.ShowErrorMessage(message))

    private fun showLoading() = updateUi(AuthEvent.ShowLoadingDialog)

    private fun hideLoading() = updateUi(AuthEvent.HideLoadingDialog)

    private fun updateUi(event: AuthEvent) = viewModelScope.launch { _eventChannel.send(event) }

}