package com.mambo.poetree.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mambo.poetree.utils.ConnectionLiveData
import com.mambo.poetree.data.local.PoemsDao
import com.mambo.poetree.data.model.Poem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val poemsDao: PoemsDao,
    application: Application
) : AndroidViewModel(application) {

    private val _connection = ConnectionLiveData(application)
    val connection: ConnectionLiveData get() = _connection

    val isLoggedIn = false
    var backIsPressed = false


    private val _poems = (poemsDao.getAll().asLiveData())
    val poems: LiveData<List<Poem>>
        get() = _poems

    fun insert(poem: Poem) {
        viewModelScope.launch {
            poemsDao.insert(poem)
        }
    }
}