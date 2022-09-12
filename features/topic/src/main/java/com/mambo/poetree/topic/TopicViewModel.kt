package com.mambo.poetree.topic

import android.graphics.Color
import androidx.lifecycle.*
import com.mambo.core.repository.TopicsRepository
import com.mambo.data.models.Topic
import com.mambo.data.requests.TopicRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopicViewModel @Inject constructor(
    private val state: SavedStateHandle
) : ViewModel() {

    @Inject
    lateinit var repository: TopicsRepository

    private val _uiEvents = MutableStateFlow<TopicEvent?>(null)
    val uiEvents: StateFlow<TopicEvent?> get() = _uiEvents

    private val _topic = state.getLiveData<Topic?>("topic", null)
    val topic: LiveData<Topic?> get() = _topic

    private val _color = MutableLiveData<String>()
    val color: LiveData<String> get() = _color

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _colors = MutableLiveData<List<String>>()
    val colors: LiveData<List<String>> get() = _colors

    init {
        generateRandomColors()
    }

    fun updateTopic(topic: Topic?) {
        topic?.let {
            _topic.value = it
            _name.value = it.name
            _color.value = it.color
        }
    }

    fun updateColor(hex: String) {
        _color.value = hex
    }

    fun updateName(s: String) {
        _name.value = s
    }

    fun generateRandomColors() {

        val list = mutableListOf<String>()

        for (number in 1 until 6) list.add(String.format("#%06X", 0xFFFFFF and generateRgb()))

        _color.value = String.format("#%06X", 0xFFFFFF and generateRgb())
        _colors.value = list
    }

    private fun generateRgb(): Int {
        val (red, green, blue) = Triple(
            first = (0 until 128).random().plus(127),
            second = (0 until 128).random().plus(127),
            third = (0 until 128).random().plus(127)
        )
        return Color.rgb(red, green, blue)
    }

    fun saveTopic(name: String, color: String) {
        when (_topic.value == null) {
            true -> create(name, color)
            false -> update(name, color)
        }
    }

    private fun create(name: String, color: String) {
        viewModelScope.launch {
            _uiEvents.value = TopicEvent.Loading(true)

            val request = TopicRequest(name = name, color = color)
            val response = repository.create(request)

            _uiEvents.value = TopicEvent.Loading(false)

            if (response.isSuccessful.not()) {
                _uiEvents.value = TopicEvent.Error(response.message)
                return@launch
            }

            _uiEvents.value = TopicEvent.Success(response.message)
        }
    }

    private fun update(name: String, color: String) {
        viewModelScope.launch {
            _uiEvents.value = TopicEvent.Loading(true)

            val id = _topic.value!!.id

            val prevName = _topic.value?.name
            val prevColor = _topic.value?.name

            if (name.equals(prevName) and color.equals(prevColor)) {
                _uiEvents.value = TopicEvent.Loading(false)
                _uiEvents.value = TopicEvent.Error("Nothing changed in topic details")
                return@launch
            }

            val request = TopicRequest(
                name = name.takeIf { it == prevName },

                color = color.takeIf { it == prevColor }
            )
            val response = repository.update(id, request)

            _uiEvents.value = TopicEvent.Loading(false)

            if (response.isSuccessful.not()) {
                _uiEvents.value = TopicEvent.Error(response.message)
                return@launch
            }

            _uiEvents.value = TopicEvent.Success(response.message)
        }
    }

    fun delete() {
        viewModelScope.launch {
            _uiEvents.value = TopicEvent.Loading(true)

            val id = _topic.value!!.id
            val response = repository.delete(id)

            _uiEvents.value = TopicEvent.Loading(false)

            if (response.isSuccessful.not()) {
                _uiEvents.value = TopicEvent.Error(response.message)
                return@launch
            }

            _uiEvents.value = TopicEvent.Success(response.message)
        }
    }

    sealed class TopicEvent {

        data class Loading(val isLoading: Boolean) : TopicEvent()
        data class Success(val message: String) : TopicEvent()
        data class Error(val message: String) : TopicEvent()

    }


}