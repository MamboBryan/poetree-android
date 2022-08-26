package com.mambobryan.features.account

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mambo.core.repository.UserRepository
import com.mambo.core.utils.toDate
import com.mambo.core.utils.toDateString
import com.mambo.data.preferences.UserPreferences
import com.mambo.data.requests.UserUpdateRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val preferences: UserPreferences
) : ViewModel() {

    @Inject
    lateinit var userRepository: UserRepository

    private val _eventChannel = Channel<AccountEvents>()
    val events = _eventChannel.receiveAsFlow()

    private var mUsername: String? = null
    private var mEmail: String? = null
    private var mDateOfBirth: String? = null
    private var mGender: Int? = null
    private var mAbout: String? = null

    val genders = listOf("male", "female")

    private val _imageUri = MutableLiveData<Uri?>(null)
    val imageUri get() = _imageUri

    private val _dob = MutableLiveData<Calendar?>(null)
    val dob get() = _dob

    private val _bio = MutableLiveData<String?>(null)
    private val _username = MutableLiveData<String?>(null)
    private val _gender = MutableLiveData<String?>(null)

    val userDetails = preferences.user

    init {

        val user = runBlocking { preferences.user.first() }

        if (user != null) {
            mUsername = user.name
            mEmail = user.email
            mDateOfBirth = user.dateOfBirth
            mGender = user.gender
            mAbout = user.bio
        }

    }

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

    fun updateUserDetails(
        username: String,
        email: String,
        gender: String,
        about: String
    ) = viewModelScope.launch {
        updateUi(AccountEvents.ShowLoadingDialog)
        try {

            val genderValue = if (gender.equals("male", true)) 1 else 0
            val dateValue = _dob.value.toDate().toDateString() ?: null

            val request = UserUpdateRequest(
                username = username.takeIf { !mUsername.equals(username) },
                email = email.takeIf { !mEmail.equals(email) },
                gender = genderValue.takeIf { mGender != genderValue },
                bio = about.takeIf { !mAbout.equals(about) },
                dateOfBirth = dateValue.takeIf {
                    !it.isNullOrBlank() or !mDateOfBirth.equals(dateValue)
                }
            )

            val response = userRepository.updateUser(request)

            if (response.isSuccessful.not()) {
                updateUi(AccountEvents.HideLoadingDialog)
                updateUi(AccountEvents.ShowError(response.message))
                return@launch
            }

            val data = response.data!!

            preferences.saveUserDetails(data)

            _imageUri.value?.let { updateUi(event = AccountEvents.StartImageUpload(it)) }

            updateUi(AccountEvents.ShowSuccess("User details updated successfully!"))
            updateUi(AccountEvents.HideLoadingDialog)

        } catch (e: Exception) {
            updateUi(AccountEvents.HideLoadingDialog)
            updateUi(AccountEvents.ShowError(e.localizedMessage ?: "Error updating details"))
        }
    }

    private fun updateUi(event: AccountEvents) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class AccountEvents {
        data class ShowError(val message: String) : AccountEvents()
        data class ShowSuccess(val message: String) : AccountEvents()
        data class StartImageUpload(val uri: Uri) : AccountEvents()
        object ShowLoadingDialog : AccountEvents()
        object HideLoadingDialog : AccountEvents()
    }


}