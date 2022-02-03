package com.mambo.core.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.mambo.data.models.Poem
import com.mambo.data.models.Topic
import com.mambo.local.PoemsDao
import com.mambo.remote.service.PoemsApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PoemRepository @Inject constructor() {

    @Inject
    lateinit var poemsDao: PoemsDao

    @Inject
    lateinit var poemsApi: PoemsApi

    fun poems(): Flow<List<Poem>> = poemsDao.getAll()

    fun feedPoems() = Pager(PagingConfig(10)) { poemsDao.getAllPoems() }.flow

    fun searchPoems(topic: Topic?, query: String) = Pager(PagingConfig(10)) {
        if (topic != null) poemsDao.getPoems(query)
        else poemsDao.getPoems(query)
    }.flow

    fun bookmarkPoems(query: String) =
        Pager(PagingConfig(10)) { poemsDao.getBookmarks(query) }.flow

    fun unpublishedPoems(userId: String, query: String) =
        Pager(PagingConfig(10)) { poemsDao.getUnPublishedPoems(userId, query) }.flow

    fun publishedPoems() =
        Pager(PagingConfig(10)) { poemsDao.getAllPoems() }.flow

    fun searchPoems(query: String) = Pager(PagingConfig(10)) { poemsDao.getPoems(query) }.flow

    suspend fun get(id:Long) = poemsDao.get(id)

    suspend fun save(poem: Poem) = poemsDao.insert(poem)

    suspend fun update(poem: Poem): Int = poemsDao.update(poem)

    suspend fun delete(poem: Poem) = poemsDao.delete(poem)

}
