package com.mambo.core.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.mambo.core.source.PoemsMediator
import com.mambo.core.source.UserPoemsMediator
import com.mambo.data.models.LocalPoem
import com.mambo.data.models.Poem
import com.mambo.data.models.Topic
import com.mambo.data.requests.CreatePoemRequest
import com.mambo.data.requests.EditPoemRequest
import com.mambo.data.requests.PoemRequest
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
        Pager(PagingConfig(10)) { poemsDao.getUnPublishedPoems(userId) }.flow

    fun publishedPoems() =
        Pager(PagingConfig(10)) { poemsDao.getAllPoems() }.flow

    fun searchPoems(query: String) = Pager(PagingConfig(10)) { poemsDao.getPoems(query) }.flow

    suspend fun save(poem: LocalPoem) = poemsDao.insert(poem)

    suspend fun update(poem: LocalPoem): Int = poemsDao.update(poem)

    suspend fun delete(poem: LocalPoem) = poemsDao.delete(poem)

    /**
     * LOCAL
     */

    // SINGLE

    suspend fun saveLocal(poem: LocalPoem) = poemsDao.insert(poem)

    suspend fun updateLocal(poem: LocalPoem) = poemsDao.update(poem)

    suspend fun deleteLocal(poem: LocalPoem) = poemsDao.delete(poem)

    // MULTIPLE

    fun localSavedPoems() = Pager(PagingConfig(20)) { poemsDao.getAllPoems() }.flow

    /**
     * REMOTE
     */

    // SINGLE

    suspend fun createPublished(request: CreatePoemRequest) = poemsApi.createPoem(request)

    suspend fun updatePublished(request: EditPoemRequest) = poemsApi.updatePoem(request)

    suspend fun deletePublished(poemId: String) = poemsApi.deletePoem(PoemRequest(poemId))

    suspend fun getPublished(poemId: String) = poemsApi.getPoem(PoemRequest(poemId))

    // MULTIPLE

    fun publishedPoems(query: String = "") = Pager(PagingConfig(20)) {
        PoemsMediator(query, poemsApi)
    }.flow

    fun getUserPoems(userId: String) = Pager(PagingConfig(20)) {
        UserPoemsMediator(userId, poemsApi)
    }.flow

}
