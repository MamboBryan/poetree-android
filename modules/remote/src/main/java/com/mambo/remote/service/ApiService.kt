package com.mambo.remote.service

import com.mambo.data.requests.SetupRequest
import com.mambo.data.requests.SignInRequest
import com.mambo.data.requests.SignUpRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/signin")
    suspend fun signIn(@Body signInRequest: SignInRequest)

    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest)

    @POST("user/setup")
    suspend fun setup(@Body request: SetupRequest)

}