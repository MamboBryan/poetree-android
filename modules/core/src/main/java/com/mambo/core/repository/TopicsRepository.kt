package com.mambo.core.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.mambo.core.source.TopicsMediator
import com.mambo.data.requests.TopicRequest
import com.mambo.data.utils.Constants.PAGE_SIZE
import com.mambo.local.daos.TopicsDao
import com.mambo.remote.service.PoemsApi
import javax.inject.Inject

class TopicsRepository @Inject constructor() {

    @Inject
    lateinit var poemsApi: PoemsApi

    @Inject
    lateinit var topicsDao: TopicsDao

    suspend fun create(request: TopicRequest) = poemsApi.createTopic(request)

    suspend fun update(topicId: Int, request: TopicRequest) = poemsApi.updateTopic(topicId, request)

    suspend fun get(topicId: Int) = poemsApi.getTopic(topicId)

    suspend fun delete(topicId: Int) = poemsApi.deleteTopic(topicId)

    @OptIn(ExperimentalPagingApi::class)
    fun getTopics() = Pager(
        config = PagingConfig(PAGE_SIZE),
        remoteMediator = TopicsMediator(query = "", topicsDao = topicsDao, poemsApi = poemsApi),
        pagingSourceFactory = { topicsDao.getTopics() }
    ).flow

    // TODO: implement search topic feature
    @OptIn(ExperimentalPagingApi::class)
    fun searchTopics(query: String) = Pager(
        config = PagingConfig(PAGE_SIZE),
        remoteMediator = TopicsMediator(query = query, topicsDao = topicsDao, poemsApi = poemsApi),
        pagingSourceFactory = { topicsDao.getTopics(query = query) }
    ).flow

}