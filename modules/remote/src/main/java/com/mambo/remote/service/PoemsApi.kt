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
        val response = client.post(Endpoints.RESET.url) {
            setBody(mapOf("email" to email))
        }
        response.body()
    }

    /**
     * CURRENT USER
     */

    suspend fun getMyDetails() = safeApiCall<UserDetails> {
        val response = client.get(Endpoints.USER_ME.url)
        response.body()
    }

    suspend fun userSetup(request: SetupRequest) = safeApiCall<UserDetails> {
        val url = Endpoints.USER_ME.url.plus("/setup")
        val response = client.post(url) {
            setBody(request)
        }
        response.body()
    }

    suspend fun updateUser(request: UserUpdateRequest) = safeApiCall<UserDetails> {
        val url = Endpoints.USER_ME.url.plus("/update")
        val response = client.put(url) {
            setBody(request)
        }
        response.body()
    }

    suspend fun updatePassword(request: UpdatePasswordRequest) = safeApiCall<TokenResponse> {
        val url = Endpoints.USER_ME.url.plus("/update-password")
        val response = client.put(url) {
            setBody(request)
        }
        response.body()
    }

    suspend fun uploadMessagingToken(token: String) = safeApiCall<UserDetails> {
        val url = Endpoints.USER_ME.url.plus("/update")
        val response = client.put(url) {
            setBody(mapOf("token" to token))
        }
        response.body()
    }

    suspend fun uploadImageUrl(imageUrl: String) = safeApiCall<UserDetails> {
        val url = Endpoints.USER_ME.url.plus("/update")
        val response = client.put(url) {
            setBody(mapOf("imageUrl" to imageUrl))
        }
        response.body()
    }

    suspend fun deleteAccount() = safeApiCall<Boolean> {
        val response = client.delete(Endpoints.USER_ME.url.plus("/delete"))
        response.body()
    }

    /**
     * USERS
     */

    suspend fun getUser(userId: String) = safeApiCall<UserDetails> {
        val response = client.post(Endpoints.USER.url) {
            setBody(mapOf("userId" to userId))
        }
        response.body()
    }

    suspend fun getUsers() = safeApiCall<PagedData<UserListResponse>> {
        val response = client.get(Endpoints.USERS.url) { }
        response.body()
    }

    suspend fun searchUsers(query: String) = safeApiCall<PagedData<UserListResponse>> {
        val response = client.get(Endpoints.USERS.url) {
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

    suspend fun createPoem(request: CreatePoemRequest) = safeApiCall<PoemResponse> {
        val url = Endpoints.POEMS.url
        val response = client.post(url) {
            setBody(request)
        }
        response.body()
    }

    suspend fun getPoem(request: PoemRequest) = safeApiCall<PoemResponse> {
        val url = Endpoints.POEM.url
        val response = client.post(url) {
            setBody(request)
        }
        response.body()
    }

    suspend fun updatePoem(request: EditPoemRequest) = safeApiCall<PoemResponse> {
        val url = Endpoints.POEM.url
        val response = client.put(url) {
            setBody(request)
        }
        response.body()
    }

    suspend fun deletePoem(poemId: String) = safeApiCall<Boolean> {
        val url = Endpoints.POEM.url
        val response = client.delete(url) {
            setBody(mapOf("poemId" to poemId))
        }
        response.body()
    }

    suspend fun markPoemAsRead(poemId: String) = safeApiCall<Boolean> {
        val url = Endpoints.POEM.url.plus("/read")
        val response = client.post(url) {
            setBody(mapOf("poemId" to poemId))
        }
        response.body()
    }

    suspend fun getPoems(page: Int = 1) = safeApiCall<PagedData<PoemResponse>> {
        val url = Endpoints.POEMS.url
        val response = client.get(url) {
            parameter("page", page.toString())
        }
        response.body()
    }

    suspend fun getUserPoems(userId: String, page: Int = 1) = safeApiCall<PagedData<PoemResponse>> {
        val url = Endpoints.USER.url.plus("/poems")
        val response = client.post(url) {
            setBody(mapOf("userId" to userId))
            parameter("page", page.toString())
        }
        response.body()
    }

    suspend fun searchPoems(query: String, topicId: Int?, page: Int = 1) =
        safeApiCall<PagedData<PoemResponse>> {
            val url = Endpoints.POEMS.url
            val response = client.get(url) {
                if (topicId != null) parameter("topic", topicId.toString())
                parameter("q", query)
                parameter("page", page.toString())
            }
            response.body()
        }

    /**
     * COMMENTS
     */

    suspend fun createComment(request: CreateCommentRequest) = safeApiCall<CommentResponse> {
        val response = client.post(Endpoints.COMMENTS.url) {
            setBody(request)
        }
        response.body()
    }

    suspend fun updateComment(request: UpdateCommentRequest) = safeApiCall<CommentResponse> {
        val response = client.put(Endpoints.COMMENTS.url) {
            setBody(request)
        }
        response.body()
    }

    suspend fun getComment(commentId: String) = safeApiCall<CompleteCommentResponse> {
        val response = client.post(Endpoints.COMMENT.url) {
            setBody(mapOf("commentId" to commentId))
        }
        response.body()
    }

    suspend fun getComments(poemId: String, page: Int = 1) =
        safeApiCall<PagedData<CompleteCommentResponse>> {

            val url = Endpoints.POEM.url.plus("/comments")

            val response = client.post(url) {
                setBody(mapOf("poemId" to poemId))
                parameter("page", page.toString())
            }
            response.body()
        }

    suspend fun deleteComment(commentId: String) = safeApiCall<Boolean> {
        val response = client.delete(Endpoints.COMMENT.url) {
            setBody(mapOf("commentId" to commentId))
        }
        response.body()
    }

    /**
     * BOOKMARKS
     */

    suspend fun bookmarkPoem(poemId: String) = safeApiCall<Boolean> {
        val url = Endpoints.POEM.url.plus("/bookmark")
        val response = client.post(url) {
            setBody(mapOf("poemId" to poemId))
        }
        response.body()
    }

    suspend fun unBookmarkPoem(poemId: String) = safeApiCall<Boolean> {
        val url = Endpoints.POEM.url.plus("/un-bookmark")
        val response = client.delete(url) {
            setBody(mapOf("poemId" to poemId))
        }
        response.body()
    }

    /**
     * LIKES
     */

    suspend fun likePoem(poemId: String) = safeApiCall<Boolean> {
        val url = Endpoints.POEM.url.plus("/like")
        val response = client.post(url) {
            setBody(mapOf("poemId" to poemId))
        }
        response.body()
    }

    suspend fun unLikePoem(poemId: String) = safeApiCall<Boolean> {
        val url = Endpoints.POEM.url.plus("/unlike")
        val response = client.delete(url) {
            setBody(mapOf("poemId" to poemId))
        }
        response.body()
    }

    suspend fun likeComment(poemId: String) = safeApiCall<Boolean> {
        val url = Endpoints.COMMENT.url.plus("/like")
        val response = client.post(url) {
            setBody(mapOf("commentId" to poemId))
        }
        response.body()
    }

    suspend fun unLikeComment(poemId: String) = safeApiCall<Boolean> {
        val url = Endpoints.COMMENT.url.plus("/unlike")
        val response = client.delete(url) {
            setBody(mapOf("commentId" to poemId))
        }
        response.body()
    }


}