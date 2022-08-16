package com.mambo.core.repository

import com.mambo.data.requests.AuthRequest
import com.mambo.data.responses.AuthResponse
import com.mambo.data.responses.ServerResponse
import com.mambo.remote.service.PoemsApi
import javax.inject.Inject

class AuthRepository @Inject constructor() {

    @Inject
    lateinit var service: PoemsApi

    suspend fun signIn(email: String, password: String): ServerResponse<AuthResponse?> {
        return service.signIn(AuthRequest(email, password))
    }

    suspend fun signUp(email: String, password: String): ServerResponse<AuthResponse?> {
        return service.signUp(AuthRequest(email, password))
    }

}