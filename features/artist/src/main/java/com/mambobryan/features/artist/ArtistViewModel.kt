package com.mambobryan.features.artist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mambo.core.repository.PoemRepository
import com.mambo.core.repository.UserRepository
import com.mambo.data.models.User
import com.mambo.data.responses.UserDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(
    private val userRepository: UserRepository,
    repository: PoemRepository,
    state: SavedStateHandle
) : ViewModel() {

    private val _eventChannel = Channel<ArtistEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val _user = state.getStateFlow<User?>("artist", null)
    val user: StateFlow<User?> get() = _user

    private val _details = MutableStateFlow<UserDetails?>(null)
    val details: StateFlow<UserDetails?> get() = _details

    private val _poems =
        _user.map { if (it != null) repository.getUserPoems(it.id!!).first() else null }
    val poems get() = _poems

    init {
        getUpdatedUserDetails()
    }

    private fun getUpdatedUserDetails() = viewModelScope.launch {

        val response = userRepository.getUserDetails(userId = _user.value!!.id!!)

        if (response.isSuccessful.not()) {
            updateUi(ArtistEvent.ShowError(response.message))
            updateUi(ArtistEvent.HideLoading)
            return@launch
        }

        val details = response.data

        details?.let { _details.value = it }

        updateUi(ArtistEvent.HideLoading)

    }

    private fun updateUi(event: ArtistEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    sealed class ArtistEvent {
        object HideLoading : ArtistEvent()
        data class ShowError(val message: String) : ArtistEvent()
    }

}