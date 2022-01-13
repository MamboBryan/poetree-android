package com.mambo.core.repository

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mambo.core.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(BuildConfig.LIBRARY_PACKAGE_NAME)

@Singleton
class PreferencesRepository @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val DARK_MODE = intPreferencesKey("dark_mode")
        val SORT_SEARCH = stringPreferencesKey("sort_search")
        val SORT_BOOKMARKS = booleanPreferencesKey("sort_bookmarks")
    }

    val darkModeFlow: Flow<Int> =
        dataStore.data.map { preferences ->
            preferences[PreferencesKeys.DARK_MODE] ?: AppCompatDelegate.MODE_NIGHT_NO
        }

    suspend fun updateDarkMode(mode: Int) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.DARK_MODE] = mode }
    }


}