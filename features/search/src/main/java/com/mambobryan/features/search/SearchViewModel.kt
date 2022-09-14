package com.mambobryan.features.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mambo.data.models.Topic
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    state: SavedStateHandle
) : ViewModel() {

    private val _topic = state.getLiveData<Topic?>("topic", null)
    val topic: LiveData<Topic?> get() = _topic

}