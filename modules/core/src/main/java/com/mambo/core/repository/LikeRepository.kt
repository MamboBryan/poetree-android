package com.mambo.core.repository

import com.mambo.remote.service.PoemsApi
import javax.inject.Inject

class LikeRepository @Inject constructor() {

    @Inject
    lateinit var poemsApi: PoemsApi

    suspend fun likePoem(poemId: String) = poemsApi.likePoem(poemId = poemId)

    suspend fun unLikePoem(poemId: String) = poemsApi.unLikePoem(poemId = poemId)

    suspend fun likeComment(commentId: String) = poemsApi.likeComment(commentId = commentId)

    suspend fun unLikeComment(commentId: String) = poemsApi.unLikeComment(commentId = commentId)

}
