package com.mambo.remote.service

import com.mambo.data.requests.SetupRequest
import com.mambo.data.requests.SignInRequest
import com.mambo.data.requests.SignUpRequest
import com.mambo.data.responses.Response
import com.mambo.data.responses.auth.SignInData
import com.mambo.data.responses.auth.SignUpData
import retrofit2.http.Body
import retrofit2.http.POST

interface PoemsApi {

    @POST("auth/signin")
    suspend fun signIn(@Body signInRequest: SignInRequest) : Response<SignInData>

    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest) : SignUpData

    @POST("user/setup")
    suspend fun setup(@Body request: SetupRequest)

    @POST("user/setup")
    suspend fun updateImageUrl(@Body image: String)

    @POST("user/update-token")
    suspend fun updateToken(@Body token: String)

}