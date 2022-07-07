package com.mambo.core.repository

import com.mambo.data.requests.SignInRequest
import com.mambo.data.requests.SignUpRequest
import com.mambo.data.responses.Response
import com.mambo.data.responses.auth.SignInData
import com.mambo.data.responses.auth.SignUpData
import com.mambo.remote.service.PoemsApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

interface AuthRepository{
    suspend fun signIn(email:String, password:String): Response<SignInData>
    suspend fun signUp(email:String, password:String): SignUpData
}

class AuthRepositoryImpl @Inject constructor(): AuthRepository {

    @Inject
    lateinit var service: PoemsApi

    override suspend fun signIn(email: String, password: String): Response<SignInData> {
        return service.signIn(SignInRequest(email, password))
    }

    override suspend fun signUp(email: String, password: String): SignUpData {
        return service.signUp(SignUpRequest(email, password, password))
    }

}