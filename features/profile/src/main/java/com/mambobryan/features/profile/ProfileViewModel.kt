package com.mambobryan.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.mambo.core.repository.PoemRepository
import com.mambo.core.repository.UserRepository
import com.mambo.data.models.Poem
import com.mambo.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val preferences: UserPreferences,
    private val userRepository: UserRepository,
    private val poemRepository: PoemRepository
) : ViewModel() {

    private val _eventChannel = Channel<ProfileEvent>()
    val events = _eventChannel.receiveAsFlow()

    var mode: Int

    val userDetails = preferences.user

    private val _poems = userDetails.map { if (it != null) poemRepository.getUserPoems(it.id!!).first() else null }
    val poems: Flow<PagingData<Poem>?> get() = _poems

    init {
        runBlocking { mode = preferences.darkMode.first() }
        getUpdatedUserDetails()
    }

    private fun getUpdatedUserDetails() = viewModelScope.launch {

        val response = userRepository.getMyDetails()

        if (response.isSuccessful.not()) {
            updateUi(ProfileEvent.ShowError(response.message))
            updateUi(ProfileEvent.HideLoading)
            return@launch
        }

        val details = response.data

        details?.let {
            preferences.saveUserDetails(details = it)
            it.image?.let { url -> preferences.updateImageUrl(url = url) }
        }

        updateUi(ProfileEvent.HideLoading)

    }

    fun onAppThemeClicked() = updateUi(ProfileEvent.ShowDarkModeDialog)

    fun onDarkModeSelected(mode: Int) = viewModelScope.launch { preferences.updateDarkMode(mode) }

    private fun updateUi(event: ProfileEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class ProfileEvent {
        object ShowDarkModeDialog : ProfileEvent()
        data class ShowError(val message: String) : ProfileEvent()
        object HideLoading : ProfileEvent()
    }

}