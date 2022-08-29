package com.mambobryan.features.publish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mambo.core.repository.PoemRepository
import com.mambo.core.repository.TopicsRepository
import com.mambo.data.models.Poem
import com.mambo.data.models.Topic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublishViewModel @Inject constructor(
    val poemRepo: PoemRepository,
    topicsRepo: TopicsRepository
) : ViewModel() {

    private val _eventChannel = Channel<TopicEvent?>()
    val events get() = _eventChannel.receiveAsFlow()

    private val query = MutableStateFlow("")
    private val topicsFlow = query.flatMapLatest { query -> topicsRepo.getTopics(query) }
    val topics = topicsFlow.cachedIn(viewModelScope)

    private val _poem = MutableStateFlow<Poem?>(null)

    private val _topic = MutableStateFlow<Topic?>(null)
    val topic get() = _topic

    fun onQueryUpdated(text: String) {
        query.value = text
    }

    fun updatePoem(poem: Poem) {
        _poem.value = poem
    }

    fun onTopicSelected(topic: Topic) {
        _topic.value = topic
    }

    fun onChooseClicked() = viewModelScope.launch(Dispatchers.IO) {

        val updatedPoem = _poem.value!!.copy(topic = topic.value)

        try {

            val id = poemRepo.update(updatedPoem)
            val poem = poemRepo.get(id.toLong())

            //update shared view-model poem
            _eventChannel.send(TopicEvent.UpdateSharedViewModelPoem(poem))
            _eventChannel.send(TopicEvent.ShowSuccessMessage("Topic Set Successfully"))

        } catch (e: Exception) {
            _eventChannel.send(TopicEvent.ShowErrorMessage(e.localizedMessage!!))
        }
    }

    sealed class TopicEvent {
        data class ShowErrorMessage(val message: String) : TopicEvent()
        data class ShowSuccessMessage(val message: String) : TopicEvent()
        data class UpdateSharedViewModelPoem(val poem: Poem) : TopicEvent()
    }


}