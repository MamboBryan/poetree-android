package com.mambobryan.features.publish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mambo.core.repository.PoemRepository
import com.mambo.core.repository.TopicsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class PublishViewModel @Inject constructor(
    poemRepo: PoemRepository,
    topicsRepo: TopicsRepository
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val topicsFlow = query.flatMapLatest { query->  topicsRepo.getTopics(query)}
    val topics = topicsFlow.cachedIn(viewModelScope)

    fun updateQuery(text: String) {
        query.value = text
    }

}