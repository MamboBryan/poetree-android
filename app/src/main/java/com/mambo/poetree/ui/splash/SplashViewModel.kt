package com.mambo.poetree.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mambo.poetree.repositories.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val darkModeFlow = preferencesRepository.darkModeFlow
    val isDarkModeEnabled = darkModeFlow.asLiveData()

}