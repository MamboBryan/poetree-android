package com.mambo.core.repository

import com.mambo.data.requests.AuthRequest
import com.mambo.remote.service.PoemsApi
import javax.inject.Inject

class AuthRepository @Inject constructor() {

    @Inject
    lateinit var poemsApi: PoemsApi

    suspend fun signIn(email: String, password: String) =
        poemsApi.signIn(AuthRequest(email, password))

    suspend fun signUp(email: String, password: String) =
        poemsApi.signUp(AuthRequest(email, password))

    suspend fun reset(email: String) = poemsApi.reset(email)

    suspend fun refreshToken(token: String) = poemsApi.refreshToken(token)

}