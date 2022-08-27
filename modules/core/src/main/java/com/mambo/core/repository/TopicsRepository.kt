package com.mambo.core.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.mambo.data.requests.TopicRequest
import com.mambo.local.TopicsDao
import com.mambo.remote.service.PoemsApi
import javax.inject.Inject

class TopicsRepository @Inject constructor() {

    @Inject
    lateinit var poemsApi: PoemsApi

    @Inject
    lateinit var topicsDao: TopicsDao

    fun topics() = topicsDao.getAll()

    fun getTopics() = Pager(PagingConfig(10)) { topicsDao.getTopics() }.flow

    fun getTopics(query: String) = Pager(PagingConfig(10)) { topicsDao.getTopics(query) }.flow

    suspend fun create(request: TopicRequest) = poemsApi.createTopic(request)

    suspend fun update(topicId: Int, request: TopicRequest) = poemsApi.updateTopic(topicId, request)

    suspend fun get(topicId: Int) = poemsApi.getTopic(topicId)

    suspend fun delete(topicId: Int) = poemsApi.deleteTopic(topicId)

    suspend fun topics(page: Int = 0) = poemsApi.getTopics(page)

}