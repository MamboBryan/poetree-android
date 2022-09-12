package com.mambo.core.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.mambo.core.source.PoemsMediator
import com.mambo.core.source.UserPoemsMediator
import com.mambo.data.models.Bookmark
import com.mambo.data.models.LocalPoem
import com.mambo.data.models.Poem
import com.mambo.data.models.Topic
import com.mambo.data.requests.CreatePoemRequest
import com.mambo.data.requests.EditPoemRequest
import com.mambo.data.requests.PoemRequest
import com.mambo.data.utils.Constants.PAGE_SIZE
import com.mambo.local.PoetreeDatabase
import com.mambo.local.daos.BookmarksDao
import com.mambo.local.daos.PoemsDao
import com.mambo.local.daos.PublishedDao
import com.mambo.remote.service.PoemsApi
import javax.inject.Inject

class PoemRepository @Inject constructor() {

    @Inject
    lateinit var database: PoetreeDatabase

    @Inject
    lateinit var poemsDao: PoemsDao

    @Inject
    lateinit var bookmarksDao: BookmarksDao

    @Inject
    lateinit var publishedDao: PublishedDao

    @Inject
    lateinit var poemsApi: PoemsApi

    fun searchPoems(topic: Topic?, query: String) = Pager(PagingConfig(10)) {
        if (topic != null) poemsDao.getPoems(query)
        else poemsDao.getPoems(query)
    }.flow

    fun searchPoems(query: String) = Pager(PagingConfig(10)) { poemsDao.getPoems(query) }.flow

    suspend fun save(poem: LocalPoem) = poemsDao.insert(poem)

    suspend fun update(poem: LocalPoem) = poemsDao.update(poem)

    suspend fun delete(poem: LocalPoem) = poemsDao.delete(poem)

    /**
     * LOCAL
     */

    suspend fun saveLocal(poem: LocalPoem) = poemsDao.insert(poem)

    suspend fun updateLocal(poem: LocalPoem) = poemsDao.update(poem)

    suspend fun getLocal(poem: Poem) = poemsDao.get(poem.id)

    suspend fun deleteLocal(poem: LocalPoem) = poemsDao.delete(poem)

    suspend fun deleteAllLocal() = poemsDao.deleteAll()

    fun localPoems(query: String = "") =
        Pager(PagingConfig(20)) { poemsDao.getPoems(query = query) }.flow

    /**
     * BOOKMARKS
     */

    suspend fun saveBookmark(bookmark: Bookmark) = bookmarksDao.insert(bookmark)

    suspend fun updateBookmark(bookmark: Bookmark) = bookmarksDao.update(bookmark)

    suspend fun deleteBookmark(bookmark: Bookmark) = bookmarksDao.delete(bookmark.id)

    suspend fun deleteAllBookmarks() = bookmarksDao.deleteAll()

    fun bookmarks(query: String) = Pager(PagingConfig(20)) {
        bookmarksDao.searchBookmarks(query = query)
    }.flow

    /**
     * REMOTE
     */

    suspend fun createPublished(request: CreatePoemRequest) = poemsApi.createPoem(request)

    suspend fun updatePublished(request: EditPoemRequest) = poemsApi.updatePoem(request)

    suspend fun deletePublished(poemId: String) = poemsApi.deletePoem(poemId = poemId)

    suspend fun getPublished(poemId: String) = poemsApi.getPoem(PoemRequest(poemId))

    suspend fun markAsRead(poemId: String) = poemsApi.markPoemAsRead(poemId)

    suspend fun bookmark(poemId: String) = poemsApi.bookmarkPoem(poemId = poemId)

    suspend fun unBookmark(poemId: String) = poemsApi.unBookmarkPoem(poemId = poemId)

    @OptIn(ExperimentalPagingApi::class)
    fun publishedPoems() = Pager(
        config = PagingConfig(PAGE_SIZE),
        remoteMediator = PoemsMediator(poemsApi = poemsApi, database = database),
        pagingSourceFactory = { publishedDao.getAll() }
    ).flow

    fun getUserPoems(userId: String) = Pager(PagingConfig(PAGE_SIZE)) {
        UserPoemsMediator(userId, poemsApi)
    }.flow

}
