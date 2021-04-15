package com.mambo.poetree.ui.compose

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mambo.poetree.data.local.EmotionsDao
import com.mambo.poetree.data.local.PoemsDao
import com.mambo.poetree.data.local.TopicsDao
import com.mambo.poetree.data.model.Emotion
import com.mambo.poetree.data.model.Poem
import com.mambo.poetree.data.model.Topic
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ComposeViewModel @Inject constructor(
    private val poemsDao: PoemsDao,
    private val emotionsDao: EmotionsDao,
    private val topicsDao: TopicsDao,
    private val state: SavedStateHandle
) : ViewModel() {

    companion object {
        const val POEM = "poem"
        const val POEM_EMOTION = "poem_emotion"
        const val POEM_TOPIC = "poem_topic"
    }

    val poem = state.get<Poem>(POEM)

    private val _emotions = emotionsDao.getAll()
    val emotions = _emotions.asLiveData()

    private val _topics = topicsDao.getAll()
    val topics = _topics.asLiveData()

    private val _composeEventChannel = Channel<ComposePoemEvent>()
    val composePoemEvent = _composeEventChannel.receiveAsFlow()

    var poemEmotion = state.get<String>(POEM_EMOTION) ?: poem?.emotion ?: ""
        set(value) {
            field = value
            state.set(POEM_EMOTION, value)
        }

    var poemTopic = state.get<String>(POEM_TOPIC) ?: poem?.topic ?: ""
        set(value) {
            field = value
            state.set(POEM_TOPIC, value)
        }

    fun onPreviousClicked(position: Int) = viewModelScope.launch {

        when {
            position > 0 -> _composeEventChannel.send(ComposePoemEvent.NavigateBack(""))
            else -> _composeEventChannel.send(ComposePoemEvent.NavigateBack("Are you sure you want to cancel composition?"))
        }

    }

    fun onNextClicked(position: Int) = viewModelScope.launch {

        when {
            position > 0 -> _composeEventChannel.send(ComposePoemEvent.NavigateBack(""))
            else -> _composeEventChannel.send(ComposePoemEvent.NavigateBack("Are you sure you want to cancel composition?"))
        }

    }

    fun onCompositionCanceled() = viewModelScope.launch {
        _composeEventChannel.send(
            ComposePoemEvent.NavigateToHomeFragment
        )
    }

    fun onPoemEmotionClicked(emotion: Emotion?) = viewModelScope.launch {

        if (emotion != null) {
            poemEmotion = emotion.name
        }

        when {
            poemEmotion.isEmpty() -> {
                _composeEventChannel.send(
                    ComposePoemEvent.NavigateNext("Choose an emotion to continue")
                )
            }
            else -> _composeEventChannel.send(ComposePoemEvent.NavigateNext(""))
        }

    }


    fun onPoemTopicClicked(topic: Topic) = viewModelScope.launch {

        poemTopic = topic.name

        val newPoem = poem!!.copy(
            emotion = poemEmotion,
            topic = poemTopic
        )

        poemsDao.insert(newPoem)

        when {
            poemTopic.isEmpty() -> {
                _composeEventChannel.send(
                    ComposePoemEvent.NavigateNext("Choose an emotion to continue")
                )
            }
            else -> _composeEventChannel.send(ComposePoemEvent.NavigateToPoem(newPoem))
        }

    }

    fun onComposePoemClicked() {

    }

    sealed class ComposePoemEvent {
        object NavigateToHomeFragment : ComposePoemEvent()
        data class NavigateBack(val message: String) : ComposePoemEvent()
        data class NavigateNext(val message: String) : ComposePoemEvent()
        data class NavigateToPoem(val poem: Poem) : ComposePoemEvent()
    }
}