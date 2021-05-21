package com.mambo.poetree.ui.dashboard

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mambo.poetree.data.model.Haiku
import com.mambo.poetree.utils.HaikuUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {

    private val _dashboardEventChannel = Channel<DashboardEvent>()
    val dashboardEvent = _dashboardEventChannel.receiveAsFlow()

    private val _allPoems = MutableStateFlow<List<Haiku>>(emptyList())
    val allPoems = _allPoems.asLiveData()

    private val _poetsPoems = MutableStateFlow<List<Haiku>>(emptyList())
    val poetsPoems = _poetsPoems.asLiveData()

    fun launch() {
        Handler(Looper.getMainLooper())
            .postDelayed({

                addPoems()

            }, 3000)

    }

    fun addPoems() = viewModelScope.launch {

        val haikuUtils = HaikuUtils()
        val poems = haikuUtils.haikus

        _allPoems.emit(poems)

    }

    fun onHaikuClicked(haiku: Haiku) {
        viewModelScope.launch {
            _dashboardEventChannel.send(DashboardEvent.NavigateToRead(haiku))
        }
    }

    sealed class DashboardEvent {
        data class NavigateToRead(val haiku: Haiku) : DashboardEvent()
    }

}