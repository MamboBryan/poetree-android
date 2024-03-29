package com.mambo.data.preferences

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.mambo.data.responses.UserDetails
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(@ApplicationContext context: Context) {

    private val Context.dataStore by preferencesDataStore(context.applicationContext.packageName)

    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val DARK_MODE = intPreferencesKey("dark_mode")
        val SORT_SEARCH = stringPreferencesKey("sort_search")
        val SORT_BOOKMARKS = stringPreferencesKey("sort_bookmarks")
        val IS_ON_BOARDED = booleanPreferencesKey("is_app_opened")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val IS_SETUP = booleanPreferencesKey("is_set_up")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_DETAILS = stringPreferencesKey("user_details")
        val FORCED_SIGN_OUT = booleanPreferencesKey("logged_out")
        val IMAGE_URL = stringPreferencesKey("image_url")
        val HAS_NETWORK_CONNECTION = booleanPreferencesKey("has_network_connection")
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

    val refreshToken = dataStore.data.map { prefs ->
        prefs[PreferencesKeys.REFRESH_TOKEN]
    }

    val imageUrl = dataStore.data.map { prefs ->
        prefs[PreferencesKeys.IMAGE_URL]
    }

    val forcedLogOut = dataStore.data.map { prefs ->
        prefs[PreferencesKeys.FORCED_SIGN_OUT]
    }

    val hasNetworkConnection = dataStore.data.map { prefs ->
        prefs[PreferencesKeys.HAS_NETWORK_CONNECTION]
    }

    suspend fun updateNetworkConnection(isConnected: Boolean) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.HAS_NETWORK_CONNECTION] = isConnected }
    }

    val user = dataStore.data.map { prefs ->
        val json = prefs[PreferencesKeys.USER_DETAILS]
        val user = Gson().fromJson(json, UserDetails::class.java)
        user
    }

    suspend fun updateImageUrl(url: String) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.IMAGE_URL] = url }
    }

    suspend fun updateDarkMode(mode: Int) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.DARK_MODE] = mode }
    }

    suspend fun onBoarded() {
        dataStore.edit { prefs -> prefs[PreferencesKeys.IS_ON_BOARDED] = true }
    }

    suspend fun signedIn() {
        dataStore.edit { prefs -> prefs[PreferencesKeys.IS_LOGGED_IN] = true }
    }

    suspend fun userHasSetup() {
        dataStore.edit { prefs -> prefs[PreferencesKeys.IS_SETUP] = true }
    }

    suspend fun forcedSignOut() {
        dataStore.edit { prefs -> prefs[PreferencesKeys.FORCED_SIGN_OUT] = true }
    }

    suspend fun updateIsUserSetup(isSetup: Boolean) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.IS_SETUP] = isSetup }
    }

    suspend fun updateTokens(access: String, refresh: String) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.ACCESS_TOKEN] = access }
        dataStore.edit { prefs -> prefs[PreferencesKeys.REFRESH_TOKEN] = refresh }
    }

    suspend fun saveUserDetails(details: UserDetails) {
        val json = Gson().toJson(details)
        dataStore.edit { prefs -> prefs[PreferencesKeys.USER_DETAILS] = json }
    }

    suspend fun signOut() {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.IS_SETUP] = false
            prefs[PreferencesKeys.IS_LOGGED_IN] = false
            prefs[PreferencesKeys.FORCED_SIGN_OUT] = false
            prefs[PreferencesKeys.ACCESS_TOKEN] = ""
            prefs[PreferencesKeys.REFRESH_TOKEN] = ""
            prefs[PreferencesKeys.USER_DETAILS] = ""
            prefs[PreferencesKeys.IMAGE_URL] = ""
        }
    }

}