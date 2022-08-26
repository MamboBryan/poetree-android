package com.mambo.remote.service

import com.mambo.data.preferences.UserPreferences
import com.mambo.data.requests.AuthRequest
import com.mambo.data.requests.SetupRequest
import com.mambo.data.requests.UserUpdateRequest
import com.mambo.data.responses.AuthResponse
import com.mambo.data.responses.ServerResponse
import com.mambo.data.responses.TokenResponse
import com.mambo.data.responses.UserDetails
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import timber.log.Timber
import javax.inject.Inject

class PoemsApi @Inject constructor(
    private val preferences: UserPreferences,
    private val poemsClient: PoemsClient
) {

    private val client: HttpClient = poemsClient.client()

    enum class Endpoints {

        SIGN_IN,
        SIGN_UP,
        REFRESH_TOKEN,
        RESET,
        USERS_ME_DETAILS,
        USER_SETUP,
        USER_UPDATE;

        private val BASE_URL = "https://mambo-poetree.herokuapp.com/v1/"

        val url: String
            get() = BASE_URL + when (this) {
                SIGN_IN -> "auth/signin"
                SIGN_UP -> "auth/signup"
                REFRESH_TOKEN -> "auth/refresh"
                RESET -> "auth/reset"
                USERS_ME_DETAILS -> "users/me"
                USER_SETUP -> "users/me/setup"
                USER_UPDATE -> "users/me/update"
            }

    }

    private suspend fun <T> safeApiCall(block: suspend () -> ServerResponse<T?>): ServerResponse<T?> {
        return try {
            return block()
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ServerResponse(isSuccessful = false, message = e.localizedMessage ?: "error")
        }
    }
    
    /**
     * AUTH
     */
    suspend fun signIn(request: AuthRequest) = safeApiCall<AuthResponse> {
        val response = client.post(Endpoints.SIGN_IN.url) { setBody(request) }
        response.body()
    }

    suspend fun signUp(request: AuthRequest) = safeApiCall<AuthResponse> {
        val response = client.post(Endpoints.SIGN_UP.url) { setBody(request) }
        response.body()
    }

    suspend fun refreshToken(token: String) = safeApiCall<TokenResponse> {
        val response = client.post(Endpoints.REFRESH_TOKEN.url) {
            setBody(mapOf("refreshToken" to token))
        }
        response.body()
    }

    /**
     * CURRENT USER
     */
    suspend fun getCurrentUserDetails() = safeApiCall<UserDetails> {
        val response = client.get(Endpoints.USERS_ME_DETAILS.url)
        response.body()
    }

    suspend fun userSetup(request: SetupRequest) = safeApiCall<UserDetails> {
        val response = client.post(Endpoints.USER_SETUP.url) { setBody(request) }
        response.body()
    }

    suspend fun updateUser(request: UserUpdateRequest) = safeApiCall<UserDetails> {
        val response = client.put(Endpoints.USER_UPDATE.url) { setBody(request) }
        response.body()
    }

    suspend fun uploadMessagingToken(token: String) = safeApiCall<UserDetails> {
        val response = client.put(Endpoints.USER_UPDATE.url) { setBody(mapOf("token" to token)) }
        response.body()
    }

    suspend fun uploadImageUrl(url: String) = safeApiCall<UserDetails> {
        val response = client.put(Endpoints.USER_UPDATE.url) { setBody(mapOf("imageUrl" to url)) }
        response.body()
    }

    /**
     * USERS
     */

}