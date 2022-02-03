package com.mambobryan.features.publish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mambo.core.repository.PoemRepository
import com.mambo.core.repository.TopicsRepository
import com.mambo.data.models.Topic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublishViewModel @Inject constructor(
    poemRepo: PoemRepository,
    topicsRepo: TopicsRepository
) : ViewModel() {

    private val query = MutableStateFlow("")

    private val topicsFlow = query.flatMapLatest { query -> topicsRepo.getTopics(query) }
    val topics = topicsFlow.cachedIn(viewModelScope)

    private val _topic = MutableStateFlow<Topic?>(null)
    val topic get() = _topic

    fun onQueryUpdated(text: String) {
        query.value = text
    }

    fun onTopicSelected(topic: Topic) {
        _topic.value = topic
    }

    fun onChooseClicked() = viewModelScope.launch {  }

}