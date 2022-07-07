package com.mambobryan.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mambo.core.repository.AuthRepositoryImpl
import com.mambo.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val preferences: UserPreferences,
    private val repository: AuthRepositoryImpl
) : ViewModel() {

    private val _eventChannel = Channel<AuthEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onSignInClicked(email: String, password: String) = viewModelScope.launch {
        showLoading()
        try {
            val response = repository.signIn(email, password)
            preferences.signedIn(response.data.token)
            showSuccess("Signed In Successfully")
//            getUserData()
            hideLoading()
        } catch (e: Exception) {
            hideLoading()
            showError(e.localizedMessage!!)
        }
    }

    fun onSignUpClicked(email: String, password: String) = viewModelScope.launch {
        showLoading()
        try {
            val response = repository.signUp(email, password)

            if (!response.isSuccessful) {
                showError(response.message)
                return@launch
            }

            preferences.signedUp(response.token)
            hideLoading()
            showSuccess("Signed Up Successfully")
            setupDailyInteractionReminder()
            updateUi(AuthEvent.NavigateToSetup)
        } catch (e: Exception) {
            hideLoading()
            showError(e.localizedMessage!!)
        }
    }

    fun onCreateClicked() = updateUi(AuthEvent.NavigateToSignUp)

    fun onSignUpLoginClicked() = updateUi(AuthEvent.NavigateToSignIn)

    private fun updateUi(event: AuthEvent) = viewModelScope.launch { _eventChannel.send(event) }

    private fun showError(message: String) = updateUi(AuthEvent.ShowErrorMessage(message))

    private fun showSuccess(message: String) = updateUi(AuthEvent.ShowSuccessMessage(message))

    private fun showLoading() = updateUi(AuthEvent.ShowLoadingDialog)

    private fun hideLoading() = updateUi(AuthEvent.HideLoadingDialog)

    private fun setupDailyInteractionReminder() = updateUi(AuthEvent.SetupDailyNotificationReminder)

    private fun getUserData() = viewModelScope.launch {
        try {
            // TODO network call for user data
//            preferences.signedIn(response.data.token)
//            preferences.setup()
            showSuccess("You're ready to go!")
            setupDailyInteractionReminder()
            hideLoading()
            updateUi(AuthEvent.NavigateToFeeds)
        } catch (e: Exception) {
            hideLoading()
            showError(e.localizedMessage!!)
        }
    }
}