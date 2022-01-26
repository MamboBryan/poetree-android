package com.mambo.core.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.mambo.data.models.Poem
import com.mambo.local.PoemsDao
import com.mambo.remote.service.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PoemRepository @Inject constructor() {

    @Inject
    lateinit var poemsDao: PoemsDao

    @Inject
    lateinit var poemsApi: ApiService

    fun poems(): Flow<List<Poem>> = poemsDao.getAll()

    fun getLocalPoems(query: String) = Pager(PagingConfig(10)) { poemsDao.getLocalPoems() }.flow

    fun searchPoems(query: String) = Pager(PagingConfig(10)) { poemsDao.getPoems(query) }.flow

    fun getPoems() = poemsDao.getLocalPoems()

    suspend fun save(poem: Poem) = poemsDao.insert(poem)

    suspend fun update(poem: Poem) = poemsDao.update(poem)

    suspend fun delete(poem: Poem) = poemsDao.delete(poem)

    suspend fun bookmark(poem: Poem) {}

    suspend fun publish(poem: Poem) {}

    suspend fun unPublish(poem: Poem) {}

    suspend fun like(poem: Poem) {}

    suspend fun unlike(poem: Poem) {}

    suspend fun read(poem: Poem) {}

    suspend fun comment(comment: String) {}

    suspend fun deleteRemote(poem: Poem) {}
}
