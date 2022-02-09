package com.mambo.core.repository

import com.mambo.remote.service.PoemsApi
import javax.inject.Inject

class UserRepository @Inject constructor() {

    @Inject
    lateinit var poemsApi: PoemsApi

    suspend fun updateToken(token:String) = poemsApi.updateToken(token)

}