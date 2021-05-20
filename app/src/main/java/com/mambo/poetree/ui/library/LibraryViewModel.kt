package com.mambo.poetree.ui.library

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mambo.poetree.data.model.Haiku
import com.mambo.poetree.utils.HaikuUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor() : ViewModel() {

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
}
