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
import com.mambo.poetree.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComposeViewModel @Inject constructor(
    private val poemsDao: PoemsDao,
    private val state: SavedStateHandle,
    emotionsDao: EmotionsDao,
    topicsDao: TopicsDao
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

    var emotion: Emotion? = state.get<Emotion>(POEM_EMOTION) ?: poem?.emotion
        set(value) {
            field = value
            state.set(POEM_EMOTION, value)
        }

    var topic: Topic? = state.get<Topic>(POEM_TOPIC) ?: poem?.topic
        set(value) {
            field = value
            state.set(POEM_TOPIC, value)
        }

    fun onPreviousClicked(position: Int) = viewModelScope.launch {

        when {
            position > 0 -> _composeEventChannel.send(ComposePoemEvent.NavigateBack(""))
            else -> onCompositionCanceled()
        }

    }

    fun onNextClicked(position: Int) = viewModelScope.launch {

        when {
            position > 0 -> onComposePoem()
            else -> _composeEventChannel.send(ComposePoemEvent.NavigateNext)
        }

    }

    fun onPoemEmotionClicked(updatedEmotion: Emotion?) =
        viewModelScope.launch {

            emotion = updatedEmotion

            _composeEventChannel.send(ComposePoemEvent.NavigateNext)

        }

    fun onPoemTopicClicked(selectedTopic: Topic) =
        viewModelScope.launch {

            topic = selectedTopic

        }

    private fun onCompositionCanceled() = viewModelScope.launch {
        _composeEventChannel.send(
            ComposePoemEvent.NavigateBack("Are you sure you want to cancel composition?")
        )
    }

    private fun onComposePoem() {

        if (emotion == null) {
            showInvalidInputMessage("Choose emotion to proceed")
            return
        }

        if (topic == null) {
            showInvalidInputMessage("Choose topic to complete")
            return
        }

        if (poem != null && poem.id != 0) {
            val updatedPoem = poem.copy(
                emotionName = emotion!!.name,
                emotion = emotion,
                topicName = topic!!.name,
                topic = topic
            )

            updatePoem(updatedPoem)

        } else {
            val newPoem = poem!!.copy(
                emotionName = emotion!!.name,
                emotion = emotion,
                topicName = topic!!.name,
                topic = topic
            )

            createPoem(newPoem)

        }


    }

    private fun updatePoem(poem: Poem) {
        viewModelScope.launch {

            poemsDao.update(poem)

            _composeEventChannel.send(
                ComposePoemEvent.NavigateToPoem(poem)
            )
        }
    }

    private fun createPoem(poem: Poem) {
        viewModelScope.launch {
            poemsDao.insert(poem)

            _composeEventChannel.send(
                ComposePoemEvent.NavigateToMyLibrary(
                    Result.RESULT_CREATE_OK
                )
            )
        }
    }

    private fun showInvalidInputMessage(message: String) {
        viewModelScope.launch {
            _composeEventChannel.send(ComposePoemEvent.ShowIncompletePoemMessage(message))
        }
    }

    sealed class ComposePoemEvent {
        object NavigateNext : ComposePoemEvent()

        data class NavigateBack(val message: String) : ComposePoemEvent()
        data class NavigateToPoem(val poem: Poem) : ComposePoemEvent()
        data class NavigateToMyLibrary(val result: Int) : ComposePoemEvent()
        data class ShowIncompletePoemMessage(val message: String) : ComposePoemEvent()
    }
}