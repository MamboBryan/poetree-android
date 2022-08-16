package com.mambobryan.features.setup

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mambo.core.repository.UserRepository
import com.mambo.core.utils.toDate
import com.mambo.core.utils.toDateString
import com.mambo.data.preferences.UserPreferences
import com.mambo.data.requests.SetupRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val preferences: UserPreferences
) : ViewModel() {

    @Inject
    lateinit var userRepository: UserRepository

    private val _eventChannel = Channel<SetupEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val _imageUri = MutableLiveData<Uri?>(null)
    val imageUri get() = _imageUri

    private val _username = MutableLiveData<String?>(null)

    private val _dob = MutableLiveData<Calendar?>(null)
    val dob get() = _dob

    val genders = listOf("male", "female")
    private val _gender = MutableLiveData<String?>(null)

    private val _bio = MutableLiveData<String?>(null)

    fun onImageSelected(uri: Uri) {
        _imageUri.value = uri
    }

    fun updateUsername(name: String) {
        _username.value = name
    }

    fun updateBio(bio: String) {
        _bio.value = bio
    }

    fun onDateSelected(calendar: Calendar) {
        _dob.value = calendar
    }

    fun onGenderSelected(index: Int) {
        _gender.value = genders[index]
    }

    private fun isValidDetails(): Boolean {
        var isValid = true

        if (_username.value.isNullOrBlank()) {
            updateUi(SetupEvent.ShowUsernameError)
            isValid = false
        }

        if (_dob.value == null) {
            updateUi(SetupEvent.ShowDOBError)
            isValid = false
        }

        if (_gender.value.isNullOrEmpty()) {
            updateUi(SetupEvent.ShowGenderError)
            isValid = false
        }

        if (_bio.value.isNullOrBlank() || _bio.value.toString().length > 140) {
            updateUi(SetupEvent.ShowBioError)
            isValid = false
        }

        return isValid

    }

    fun onSaveClicked() {
        if (isValidDetails()) saveUserDetails()
    }

    fun onRetryClicked() {
        saveUserDetails()
    }

    private fun saveUserDetails() = viewModelScope.launch {
        updateUi(SetupEvent.ShowLoadingDialog)
        try {

            val request = SetupRequest(
                username = _username.value!!,
                gender = if (_gender.value.equals("male", true)) 1 else 0,
                bio = _bio.value!!,
                dateOfBirth = _dob.value.toDate().toDateString()!!
            )

            val response = userRepository.setup(request)

            if (response.isSuccessful.not()) {
                updateUi(SetupEvent.HideLoadingDialog)
                updateUi(SetupEvent.ShowSetupError(response.message))
                return@launch
            }

            preferences.userHasSetup()
            getUserDetails()

        } catch (e: Exception) {
            updateUi(SetupEvent.HideLoadingDialog)
            updateUi(SetupEvent.ShowSetupError(e.localizedMessage ?: "Error saving details"))
        }
    }

    private fun getUserDetails() = viewModelScope.launch {
        try {

            val response = userRepository.getUser()

            if (!response.isSuccessful) {
                updateUi(SetupEvent.ShowSetupError(response.message))
                updateUi(SetupEvent.HideLoadingDialog)
                return@launch
            }

            val data = response.data!!

            preferences.saveUserDetails(data)

            _imageUri.value?.let { updateUi(event = SetupEvent.StartImageUpload(it)) }

            updateUi(SetupEvent.ShowSetupSuccess("You're ready to go!"))
            updateUi(SetupEvent.HideLoadingDialog)
            updateUi(SetupEvent.NavigateToFeeds)

        } catch (e: Exception) {
            updateUi(SetupEvent.HideLoadingDialog)
            updateUi(SetupEvent.ShowSetupError(e.localizedMessage ?: "Failed getting details"))
        }
    }

    private fun updateUi(event: SetupEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class SetupEvent {
        object NavigateToFeeds : SetupEvent()
        object ShowUsernameError : SetupEvent()
        object ShowDOBError : SetupEvent()
        object ShowGenderError : SetupEvent()
        object ShowBioError : SetupEvent()
        object ShowLoadingDialog : SetupEvent()
        object HideLoadingDialog : SetupEvent()
        data class StartImageUpload(val uri: Uri) : SetupEvent()
        data class ShowSetupError(val message: String) : SetupEvent()
        data class ShowSetupSuccess(val message: String) : SetupEvent()
    }
}