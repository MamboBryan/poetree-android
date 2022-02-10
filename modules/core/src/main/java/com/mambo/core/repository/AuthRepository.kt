package com.mambo.core.repository

import com.mambo.data.requests.SignInRequest
import com.mambo.data.responses.Response
import com.mambo.data.responses.auth.SignInData
import com.mambo.remote.service.PoemsApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

interface AuthRepository{
    suspend fun signIn(email:String, password:String): Response<SignInData>
}

class AuthRepositoryImpl @Inject constructor(): AuthRepository {

    @Inject
    lateinit var service: PoemsApi

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun signIn(email: String, password: String): Response<SignInData> {
        return service.signIn(SignInRequest(email, password))
    }

}