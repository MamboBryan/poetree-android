package com.mambo.core.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.mambo.local.TopicsDao
import javax.inject.Inject

class TopicsRepository @Inject constructor() {

    @Inject
    lateinit var topicsDao: TopicsDao

    fun topics() = topicsDao.getAll()

    fun getTopics() = Pager(PagingConfig(10)) { topicsDao.getTopics() }.flow

    fun getTopics(query:String) = Pager(PagingConfig(10)) { topicsDao.getTopics(query) }.flow

}