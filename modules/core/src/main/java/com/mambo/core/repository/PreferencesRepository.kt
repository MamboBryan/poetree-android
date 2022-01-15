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
        val SORT_BOOKMARKS = stringPreferencesKey("sort_bookmarks")
        val IS_ON_BOARDED = booleanPreferencesKey("is_app_opened")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val IS_SETUP = booleanPreferencesKey("is_set_up")
    }

    val darkModeFlow: Flow<Int> =
        dataStore.data.map { preferences ->
            preferences[PreferencesKeys.DARK_MODE] ?: AppCompatDelegate.MODE_NIGHT_NO
        }

    val isOnBoarded = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_ON_BOARDED] ?: false
    }

    val isLoggedIn = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_LOGGED_IN] ?: true
    }

    val isUserSetup = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_SETUP] ?: false
    }

    suspend fun updateDarkMode(mode: Int) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.DARK_MODE] = mode }
    }

    suspend fun updateOnBoarded(){
        dataStore.edit { prefs -> prefs[PreferencesKeys.IS_ON_BOARDED] = true }
    }

    suspend fun updateLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.IS_LOGGED_IN] = true }
    }

    suspend fun updateSetupStatus(isSetup: Boolean){
        dataStore.edit { prefs -> prefs[PreferencesKeys.IS_SETUP] = true }
    }

}