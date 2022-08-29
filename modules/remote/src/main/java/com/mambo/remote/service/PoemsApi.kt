package com.mambo.remote.service

import com.mambo.data.models.Topic
import com.mambo.data.preferences.UserPreferences
import com.mambo.data.requests.*
import com.mambo.data.responses.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
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
        USERS,
        USER,
        USER_ME,
        TOPICS,
        TOPIC,
        POEMS,
        POEM,
        COMMENTS,
        COMMENT,
        ;

        private val BASE_URL = "https://mambo-poetree.herokuapp.com/v1/"

        val url: String
            get() = BASE_URL + when (this) {
                SIGN_IN -> "auth/signin"
                SIGN_UP -> "auth/signup"
                REFRESH_TOKEN -> "auth/refresh"
                RESET -> "auth/reset"
                TOPIC -> "topics/topic"
                TOPICS -> "topics"
                POEM -> "poems/poem"
                POEMS -> "poems"
                USER -> "users/user"
                USERS -> "users"
                USER_ME -> "users/me"
                COMMENT -> "comments/comment"
                COMMENTS -> "comments"
            }

    }

    private suspend fun <T> safeApiCall(
        block: suspend () -> ServerResponse<T?>
    ): ServerResponse<T?> {
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

    suspend fun reset(email: String) = safeApiCall<Boolean> {
        val response = client.post {
            url(Endpoints.RESET.url)
            setBody(mapOf("email" to email))
        }
        response.body()
    }

    /**
     * CURRENT USER
     */

    suspend fun getMyDetails() = safeApiCall<UserDetails> {
        val response = client.get {
            url(Endpoints.USER_ME.url)
        }
        response.body()
    }

    suspend fun userSetup(request: SetupRequest) = safeApiCall<UserDetails> {
        val response = client.post {
            url(Endpoints.USER_ME.url) {
                path("setup")
            }
            setBody(request)
        }
        response.body()
    }

    suspend fun updateUser(request: UserUpdateRequest) = safeApiCall<UserDetails> {
        val response = client.put {
            url(Endpoints.USER_ME.url) {
                path("update")
            }
            setBody(request)
        }
        response.body()
    }

    suspend fun updatePassword(request: UpdatePasswordRequest) = safeApiCall<TokenResponse> {
        val response = client.put {
            url(Endpoints.USER_ME.url) {
                path("update-password")
            }
            setBody(request)
        }
        response.body()
    }

    suspend fun uploadMessagingToken(token: String) = safeApiCall<UserDetails> {
        val response = client.put {
            url(Endpoints.USER_ME.url) { path("update") }
            setBody(mapOf("token" to token))
        }
        response.body()
    }

    suspend fun uploadImageUrl(url: String) = safeApiCall<UserDetails> {
        val response = client.put {
            url(Endpoints.USER_ME.url) { path("update") }
            setBody(mapOf("imageUrl" to url))
        }
        response.body()
    }

    suspend fun deleteAccount() = safeApiCall<Boolean> {
        val response = client.delete {
            url(Endpoints.USER_ME.url) { path("delete") }
        }
        response.body()
    }

    /**
     * USERS
     */

    suspend fun getUser(userId: String) = safeApiCall<UserDetails> {
        val response = client.post {
            url(Endpoints.USER.url)
            setBody(mapOf("userId" to userId))
        }
        response.body()
    }

    suspend fun getUsers() = safeApiCall<PagedData<UserListResponse>> {
        val response = client.get(Endpoints.USERS.url) { }
        response.body()
    }

    suspend fun searchUsers(query: String) = safeApiCall<PagedData<UserListResponse>> {
        val response = client.get {
            url(Endpoints.USERS.url)
            parameter("name", query)
        }
        response.body()
    }

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

    /**
     * POEMS
     */

    suspend fun createPoem(request: EditPoemRequest) = safeApiCall<PoemResponse> {
        val response = client.post {
            url(Endpoints.POEMS.url)
            setBody(request)
        }
        response.body()
    }

    suspend fun getPoem(request: PoemRequest) = safeApiCall<PoemResponse> {
        val response = client.post {
            url(Endpoints.POEM.url)
            setBody(request)
        }
        response.body()
    }

    suspend fun updatePoem(request: EditPoemRequest) = safeApiCall<PoemResponse> {
        val response = client.put {
            url(Endpoints.POEM.url)
            setBody(request)
        }
        response.body()
    }

    suspend fun deletePoem(request: PoemRequest) = safeApiCall<Boolean> {
        val response = client.delete {
            url(Endpoints.POEM.url)
            setBody(request)
        }
        response.body()
    }

    suspend fun markPoemAsRead(request: PoemRequest) = safeApiCall<Any> {
        val response = client.post {
            url(Endpoints.POEM.url) { path("read") }
            setBody(request)
        }
        response.body()
    }

    suspend fun getPoems(page: Int = 1) = safeApiCall<PagedData<PoemResponse>> {
        val response = client.get {
            url(Endpoints.POEMS.url) {
                parameter("page", page.toString())
            }
        }
        response.body()
    }

    suspend fun getUserPoems(userId: String, page: Int = 1) = safeApiCall<PagedData<PoemResponse>> {
        val response = client.get {
            url(Endpoints.USER.url) {
                path("poems")
                parameter("page", page.toString())
            }
        }
        response.body()
    }

    suspend fun searchPoems(query: String, topic: Int?, page: Int = 1) =
        safeApiCall<PagedData<PoemResponse>> {
            val response = client.get() {
                url(Endpoints.POEMS.url) {
                    if (topic != null) parameter("topic", topic.toString())
                    parameter("q", query)
                    parameter("page", page.toString())
                }
            }
            response.body()
        }

    /**
     * COMMENTS
     */

    suspend fun createComment(request: CreateCommentRequest) = safeApiCall<CommentResponse> {
        val response = client.post {
            url(Endpoints.COMMENTS.url)
            setBody(request)
        }
        response.body()
    }

    suspend fun updateComment(request: UpdateCommentRequest) = safeApiCall<CommentResponse> {
        val response = client.put {
            url(Endpoints.COMMENTS.url)
            setBody(request)
        }
        response.body()
    }

    suspend fun getComment(commentId: String) = safeApiCall<CompleteCommentResponse> {
        val response = client.post {
            url(Endpoints.COMMENT.url)
            setBody(mapOf("commentId" to commentId))
        }
        response.body()
    }

    suspend fun deleteComment(commentId: String) = safeApiCall<Boolean> {
        val response = client.delete {
            url(Endpoints.COMMENT.url)
            setBody(mapOf("commentId" to commentId))
        }
        response.body()
    }

    /**
     * BOOKMARKS
     */

    suspend fun bookmarkPoem(poemId: String) = safeApiCall<Boolean> {
        val response = client.post {
            url(Endpoints.POEM.url) { path("bookmark") }
            setBody(mapOf("poemId" to poemId))
        }
        response.body()
    }

    suspend fun unBookmarkPoem(poemId: String) = safeApiCall<Boolean> {
        val response = client.delete {
            url(Endpoints.POEM.url) { path("un-bookmark") }
            setBody(mapOf("poemId" to poemId))
        }
        response.body()
    }

    /**
     * LIKES
     */

    suspend fun likePoem(poemId: String) = safeApiCall<Boolean> {
        val response = client.post {
            url(Endpoints.POEM.url) { path("like") }
            setBody(mapOf("poemId" to poemId))
        }
        response.body()
    }

    suspend fun unLikePoem(poemId: String) = safeApiCall<Boolean> {
        val response = client.delete {
            url(Endpoints.POEM.url) { path("unlike") }
            setBody(mapOf("poemId" to poemId))
        }
        response.body()
    }

    suspend fun likeComment(poemId: String) = safeApiCall<Boolean> {
        val response = client.post {
            url(Endpoints.COMMENT.url) { path("like") }
            setBody(mapOf("commentId" to poemId))
        }
        response.body()
    }

    suspend fun unLikeComment(poemId: String) = safeApiCall<Boolean> {
        val response = client.delete {
            url(Endpoints.COMMENT.url) { path("unlike") }
            setBody(mapOf("commentId" to poemId))
        }
        response.body()
    }


}