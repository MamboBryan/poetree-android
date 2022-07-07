package com.mambo.data.preferences

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.mambo.data.BuildConfig
import com.mambo.data.models.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(BuildConfig.LIBRARY_PACKAGE_NAME)

@Singleton
class UserPreferences @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val DARK_MODE = intPreferencesKey("dark_mode")
        val SORT_SEARCH = stringPreferencesKey("sort_search")
        val SORT_BOOKMARKS = stringPreferencesKey("sort_bookmarks")
        val IS_ON_BOARDED = booleanPreferencesKey("is_app_opened")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val IS_SETUP = booleanPreferencesKey("is_set_up")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val MESSAGE_TOKEN = stringPreferencesKey("message_token")
        val USER_DETAILS = stringPreferencesKey("user_details")
        val IMAGE_URL = stringPreferencesKey("image_url")
    }

    val darkMode = dataStore.data.map { prefs ->
        prefs[PreferencesKeys.DARK_MODE] ?: AppCompatDelegate.MODE_NIGHT_NO
    }

    val isOnBoarded = dataStore.data.map { prefs ->
        prefs[PreferencesKeys.IS_ON_BOARDED] ?: false
    }

    val isLoggedIn = dataStore.data.map { prefs ->
        prefs[PreferencesKeys.IS_LOGGED_IN] ?: false
    }

    val isUserSetup = dataStore.data.map { prefs ->
        prefs[PreferencesKeys.IS_SETUP] ?: false
    }

    val accessToken = dataStore.data.map { prefs ->
        prefs[PreferencesKeys.ACCESS_TOKEN]
    }

    val imageUrl = dataStore.data.map { prefs ->
        prefs[PreferencesKeys.IMAGE_URL]
    }

    val user = dataStore.data.map { prefs ->
        val json = prefs[PreferencesKeys.USER_DETAILS]
        val user = Gson().fromJson(json, User::class.java) ?: null
        user
    }

    suspend fun updateImageUrl(url: String) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.IMAGE_URL] = url }
    }

    suspend fun updateDarkMode(mode: Int) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.DARK_MODE] = mode }
    }

    suspend fun updateOnBoarded() {
        dataStore.edit { prefs -> prefs[PreferencesKeys.IS_ON_BOARDED] = true }
    }

    suspend fun updateLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.IS_LOGGED_IN] = true }
    }

    suspend fun updateSetupStatus(isSetup: Boolean) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.IS_SETUP] = true }
    }

    suspend fun updateAccessToken(token: String) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.ACCESS_TOKEN] = token }
    }

    suspend fun updateUserDetails(user: User) {
        val json = Gson().toJson(user)
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.USER_DETAILS] = json
        }
    }

    suspend fun signedIn(token: String) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.IS_SETUP] = true
            prefs[PreferencesKeys.IS_LOGGED_IN] = true
            prefs[PreferencesKeys.ACCESS_TOKEN] = token
        }
    }

    suspend fun signedUp(token: String) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.IS_LOGGED_IN] = true
            prefs[PreferencesKeys.ACCESS_TOKEN] = token
        }
    }

    suspend fun updateUserData(){
        val json = Gson().toJson(user)
        dataStore.edit { prefs->
            prefs[PreferencesKeys.USER_DETAILS] = json
        }
    }

    suspend fun setup(user: User){
        val json = Gson().toJson(user)
        dataStore.edit { prefs->
            prefs[PreferencesKeys.IS_SETUP] = true
            prefs[PreferencesKeys.USER_DETAILS] = json
        }
    }

    suspend fun signOut() {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.IS_SETUP] = false
            prefs[PreferencesKeys.IS_LOGGED_IN] = false
            prefs[PreferencesKeys.ACCESS_TOKEN] = ""
        }
    }


}