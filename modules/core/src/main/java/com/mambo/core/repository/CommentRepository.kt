package com.mambo.core.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.mambo.core.source.CommentsMediator
import com.mambo.data.requests.CreateCommentRequest
import com.mambo.data.requests.UpdateCommentRequest
import com.mambo.remote.service.PoemsApi
import javax.inject.Inject

class CommentRepository @Inject constructor() {

    @Inject
    lateinit var poemsApi: PoemsApi

    // SINGLE

    suspend fun create(request: CreateCommentRequest) = poemsApi.createComment(request = request)

    suspend fun update(request: UpdateCommentRequest) = poemsApi.updateComment(request = request)

    suspend fun delete(commentId: String) = poemsApi.deleteComment(commentId = commentId)

    suspend fun get(commentId: String) = poemsApi.getComment(commentId = commentId)

    // MULTIPLE

    fun getComments(poemId: String) = Pager(PagingConfig(20)) {
        CommentsMediator(poemId, poemsApi)
    }.flow

}
