package com.mambobryan.features.update_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mambo.core.repository.UserRepository
import com.mambo.data.preferences.UserPreferences
import com.mambo.data.requests.UpdatePasswordRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdatePasswordViewModel @Inject constructor(
    private val repository: UserRepository,
    private val preferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<UpdateEvents?>(null)
    val uiState: StateFlow<UpdateEvents?>
        get() = _uiState

    fun updatePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {

            _uiState.value = UpdateEvents.Loading(true)

            val request = UpdatePasswordRequest(
                oldPassword = oldPassword,
                newPassword = newPassword
            )

            val response = repository.updatePassword(request = request)

            if (response.isSuccessful.not()) {
                _uiState.value = UpdateEvents.Loading(false)
                _uiState.value = UpdateEvents.Error(message = response.message)
                return@launch
            }

            val data = response.data!!

            preferences.updateTokens(access = data.accessToken, refresh = data.refreshToken)
            _uiState.value = UpdateEvents.Loading(false)
            _uiState.value = UpdateEvents.Success(message = response.message)

        }
    }

    sealed class UpdateEvents {
        data class Loading(val isLoading: Boolean) : UpdateEvents()
        data class Success(val message: String) : UpdateEvents()
        data class Error(val message: String) : UpdateEvents()
    }

}
