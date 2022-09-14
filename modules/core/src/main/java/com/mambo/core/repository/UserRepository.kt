package com.mambo.core.repository

import com.mambo.data.requests.SetupRequest
import com.mambo.data.requests.UpdatePasswordRequest
import com.mambo.data.requests.UserUpdateRequest
import com.mambo.remote.service.PoemsApi
import javax.inject.Inject

class UserRepository @Inject constructor() {

    @Inject
    lateinit var poemsApi: PoemsApi

    suspend fun getMyDetails() = poemsApi.getMyDetails()

    suspend fun getUserDetails(userId: String) = poemsApi.getUser(userId = userId)

    suspend fun setup(request: SetupRequest) = poemsApi.userSetup(request)

    suspend fun updateUser(request: UserUpdateRequest) = poemsApi.updateUser(request)

    suspend fun updateToken(token: String) = poemsApi.uploadMessagingToken(token)

    suspend fun updateImageUrl(url: String) = poemsApi.uploadImageUrl(url)

    suspend fun updatePassword(request: UpdatePasswordRequest) = poemsApi.updatePassword(request)

}