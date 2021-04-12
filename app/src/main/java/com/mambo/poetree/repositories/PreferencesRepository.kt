package com.mambo.poetree.repositories

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("poetree_preferences")

@Singleton
class PreferencesRepository @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    suspend fun updateDarkMode(isDarkModeEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = isDarkModeEnabled
        }
    }

    val darkModeFlow: Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[PreferencesKeys.DARK_MODE] ?: false
        }

    val preferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            }
        }
        .map { preferences ->
            val darkMode = preferences[PreferencesKeys.DARK_MODE] ?: 0
        }

}