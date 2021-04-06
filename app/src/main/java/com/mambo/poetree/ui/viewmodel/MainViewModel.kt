package com.mambo.poetree.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mambo.poetree.ConnectionLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _connection = ConnectionLiveData(application)
    val connection: ConnectionLiveData
        get() = _connection

}