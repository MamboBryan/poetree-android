package com.mambobryan.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mambo.core.repository.AuthRepository
import com.mambo.core.repository.UserRepository
import com.mambo.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val preferences: UserPreferences,
) : ViewModel() {

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var userRepository: UserRepository

    private val _eventChannel = Channel<AuthEvent>()
    val events = _eventChannel.receiveAsFlow()

    private fun getUserDetails() = viewModelScope.launch {
        try {

            val response = userRepository.getUser()

            if (!response.isSuccessful) {
                showError(response.message)
                hideLoading()
                return@launch
            }

            val data = response.data!!

            preferences.saveUserDetails(data)
            if (data.image.isNullOrBlank().not()) preferences.updateImageUrl(data.image!!)
            showSuccess("You're ready to go!")
            setupDailyInteractionReminder()
            hideLoading()
            updateUi(AuthEvent.NavigateToFeeds)
        } catch (e: Exception) {
            hideLoading()
            showError(e.localizedMessage!!)
        }
    }

    fun onSignInClicked(email: String, password: String) = viewModelScope.launch {
        showLoading()
        try {
            val response = authRepository.signIn(email, password)

            if (!response.isSuccessful) {
                showError(response.message)
                hideLoading()
                return@launch
            }

            showSuccess(response.message)

            val (user, token) = response.data!!.let { Pair(it.user, it.token) }

            preferences.updateAccessToken(token = token)
            preferences.updateIsUserSetup(isSetup = user.isSetup)
            preferences.signedIn()

            if (user.isSetup.not()) {
                hideLoading()
                updateUi(AuthEvent.NavigateToSetup)
                return@launch
            }

            getUserDetails()

        } catch (e: Exception) {
            hideLoading()
            showError(e.localizedMessage ?: "Error while signing in")
        }
    }

    fun onSignUpClicked(email: String, password: String) = viewModelScope.launch {
        showLoading()
        try {
            val response = authRepository.signUp(email, password)

            if (response.isSuccessful.not()) {
                showError(response.message)
                hideLoading()
                return@launch
            }

            val token = response.data!!.token

            preferences.updateAccessToken(token = token)
            preferences.signedIn()

            showSuccess("Let's set you up!")
            hideLoading()

            updateUi(AuthEvent.NavigateToSetup)

        } catch (e: Exception) {
            hideLoading()
            showError(e.localizedMessage ?: "Error while signing up")
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

}