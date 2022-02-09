package com.mambo.core.repository

import com.mambo.data.requests.SignInRequest
import com.mambo.data.requests.SignUpRequest
import com.mambo.remote.service.PoemsApi
import javax.inject.Inject

class AuthRepository @Inject constructor() {

    @Inject
    lateinit var service: PoemsApi

    suspend fun signIn(email: String, password: String) =
        service.signIn(SignInRequest(email, password))

    suspend fun signUp(email: String, password: String, confirmPass: String)=
        service.signUp(SignUpRequest(email, password, confirmPass))

}