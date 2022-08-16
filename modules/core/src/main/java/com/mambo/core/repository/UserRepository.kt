package com.mambo.core.repository

import com.mambo.data.models.User
import com.mambo.remote.service.PoemsApi
import javax.inject.Inject

class UserRepository @Inject constructor() {

    @Inject
    lateinit var poemsApi: PoemsApi

    suspend fun getUserDetails() = poemsApi.getCurrentUserDetails()

    suspend fun setup(user: User) {
//        return poemsApi.setup(
//            SetupRequest(
//                user.username,
//                user.dateOfBirth.getString(),
//                user.gender,
//                user.bio
//            )
//        )
    }

    suspend fun updateToken(token: String) {
//        return poemsApi.updateToken(token)
    }

    suspend fun updateImageUrl(url: String) {
//        return poemsApi.updateImageUrl(url)
    }

}