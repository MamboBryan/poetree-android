package com.mambo.core.repository

import com.mambo.data.models.User
import com.mambo.data.requests.SetupRequest
import com.mambo.remote.service.PoemsApi
import javax.inject.Inject

class UserRepository @Inject constructor() {

    @Inject
    lateinit var poemsApi: PoemsApi

    suspend fun getUser() = poemsApi.getCurrentUserDetails()

    suspend fun setup(request: SetupRequest) = poemsApi.userSetup(request)

    suspend fun updateToken(token: String) = poemsApi.uploadMessagingToken(token)

    suspend fun updateImageUrl(url: String) = poemsApi.uploadImageUrl(url)

}