package com.mambo.core.repository

import com.mambo.local.TopicsDao
import javax.inject.Inject

class TopicsRepository @Inject constructor() {

    @Inject
    lateinit var topicsDao: TopicsDao

    fun topics() = topicsDao.getAll()

}