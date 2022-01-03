package com.mambo.poetree.ui.read

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mambo.data.Haiku
import com.mambo.data.HaikuComment
import com.mambo.poetree.utils.HaikuUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadViewModel @Inject constructor(
    state: SavedStateHandle
) : ViewModel() {

    val haiku = state.get<Haiku>("haiku")

    private val _comments = MutableStateFlow<List<HaikuComment>>(emptyList())
    val comments = _comments.asLiveData()

    fun launch() {
        Handler(Looper.getMainLooper())
            .postDelayed({

                addComments()

            }, 3000)

    }

    private fun addComments() = viewModelScope.launch {

        val haikuUtils = HaikuUtils()
        val comments = haikuUtils.haikuComments

        _comments.emit(comments)

    }

}