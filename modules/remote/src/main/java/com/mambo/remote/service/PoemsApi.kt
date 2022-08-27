package com.mambo.remote.service

import com.mambo.data.models.Topic
import com.mambo.data.preferences.UserPreferences
import com.mambo.data.requests.*
import com.mambo.data.responses.*
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
        USER_UPDATE,
        USER_UPDATE_PASSWORD,
        TOPICS,
        ;

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
                USER_UPDATE_PASSWORD -> "users/me/update-password"
                TOPICS -> "topics"
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

    suspend fun updatePassword(request: UpdatePasswordRequest) = safeApiCall<TokenResponse> {
        val response = client.put(Endpoints.USER_UPDATE_PASSWORD.url) { setBody(request) }
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

    /**
     * TOPICS
     */
    suspend fun createTopic(request: TopicRequest) = safeApiCall<Topic> {
        val response = client.post(Endpoints.TOPICS.url) { setBody(request) }
        response.body()
    }

    suspend fun updateTopic(topicId: Int, request: TopicRequest) = safeApiCall<Topic> {
        val url = Endpoints.TOPICS.url.plus("/$topicId")
        val response = client.put(url) { setBody(request) }
        response.body()
    }

    suspend fun getTopic(topicId: Int) = safeApiCall<Topic> {
        val url = Endpoints.TOPICS.url.plus("/$topicId")
        val response = client.get(url)
        response.body()
    }

    suspend fun deleteTopic(topicId: Int) = safeApiCall<Boolean> {
        val url = Endpoints.TOPICS.url.plus("/$topicId")
        val response = client.delete(url)
        response.body()
    }

    suspend fun getTopics(page: Int = 1) = safeApiCall<PagedData<TopicDTO>> {
        val url = Endpoints.TOPICS.url
        val response = client.get(url) {
            url { parameters.append("page", page.toString()) }
        }
        response.body()
    }

}