package com.mambo.features.home

import androidx.lifecycle.ViewModel
import com.mambo.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    preferences: UserPreferences
) : ViewModel() {

    val imageUrl = preferences.imageUrl

}