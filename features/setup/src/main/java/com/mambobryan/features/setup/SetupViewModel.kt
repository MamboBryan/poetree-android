package com.mambobryan.features.setup

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(): ViewModel() {

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


    fun onImageUploadClicked() = updateUi(SetupEvent.OpenImagePicker)
    fun onImageSelected(uri: Uri){
        _imageUri.value = uri
    }

    fun updateUsername(name:String){
        _username.value = name
    }

    fun updateBio(bio:String){
        _bio.value = bio
    }

    fun onDateClicked() = updateUi(SetupEvent.OpenDatePicker)
    fun onDateSelected(calendar: Calendar){
        _dob.value = calendar
    }

    fun onGenderSelected(index:Int){
        _gender.value = genders[index]
    }

    private fun isValidDetails():Boolean {
        var isValid = true

        if(_username.value.isNullOrEmpty()){
            updateUi(SetupEvent.ShowUsernameError)
            isValid = false
        }

        if(_dob.value == null){
            updateUi(SetupEvent.ShowDOBError)
            isValid = false
        }

        if(_gender.value.isNullOrEmpty()){
            updateUi(SetupEvent.ShowGenderError)
            isValid = false
        }

        if(_bio.value.isNullOrEmpty()){
            updateUi(SetupEvent.ShowBioError)
            isValid = false
        }

        return isValid

    }

    fun onSaveClicked(){
        if(isValidDetails())
            saveUserDetails()
    }

    private fun saveUserDetails(){
        updateUi(SetupEvent.NavigateToHome)
    }


    private fun updateUi(event: SetupEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class SetupEvent{
        object OpenImagePicker : SetupEvent()
        object OpenDatePicker : SetupEvent()
        object NavigateToHome : SetupEvent()
        object ShowUsernameError : SetupEvent()
        object ShowDOBError : SetupEvent()
        object ShowGenderError : SetupEvent()
        object ShowBioError : SetupEvent()
    }
}