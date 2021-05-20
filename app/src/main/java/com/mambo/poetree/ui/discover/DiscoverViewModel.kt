package com.mambo.poetree.ui.discover

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mambo.poetree.data.model.HaikuEmotion
import com.mambo.poetree.data.model.HaikuTopic
import com.mambo.poetree.utils.HaikuUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor() : ViewModel() {

    private val _topics = MutableStateFlow<List<HaikuTopic>>(emptyList())
    val topics = _topics.asLiveData()

    private val _emotions = MutableStateFlow<List<HaikuEmotion>>(emptyList())
    val emotions = _emotions.asLiveData()

    fun launch() {
        Handler(Looper.getMainLooper())
            .postDelayed({

                addTopics()
                addEmotions()

            }, 3000)

    }

    private fun addTopics() = viewModelScope.launch {

        val haikuUtils = HaikuUtils()
        val topics = haikuUtils.haikuTopics

        _topics.emit(topics)

    }

    private fun addEmotions() = viewModelScope.launch {

        val haikuUtils = HaikuUtils()
        val emotions = haikuUtils.haikuEmotions

        _emotions.emit(emotions)

    }


}