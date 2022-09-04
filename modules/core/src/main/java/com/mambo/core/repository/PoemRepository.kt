package com.mambo.core.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.mambo.core.source.PoemsMediator
import com.mambo.core.source.UserPoemsMediator
import com.mambo.data.models.Bookmark
import com.mambo.data.models.LocalPoem
import com.mambo.data.models.Topic
import com.mambo.data.requests.CreatePoemRequest
import com.mambo.data.requests.EditPoemRequest
import com.mambo.data.requests.PoemRequest
import com.mambo.local.BookmarksDao
import com.mambo.local.PoemsDao
import com.mambo.remote.service.PoemsApi
import javax.inject.Inject

class PoemRepository @Inject constructor() {

    @Inject
    lateinit var poemsDao: PoemsDao

    @Inject
    lateinit var bookmarksDao: BookmarksDao

    @Inject
    lateinit var poemsApi: PoemsApi

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

    suspend fun saveLocal(poem: LocalPoem) = poemsDao.insert(poem)

    suspend fun updateLocal(poem: LocalPoem) = poemsDao.update(poem)

    suspend fun deleteLocal(poem: LocalPoem) = poemsDao.delete(poem)

    fun localSavedPoems() = Pager(PagingConfig(20)) { poemsDao.getAllPoems() }.flow

    /**
     * BOOKMARKS
     */

    suspend fun saveBookmark(bookmark: Bookmark) = bookmarksDao.insert(bookmark)

    suspend fun updateBookmark(bookmark: Bookmark) = bookmarksDao.update(bookmark)

    suspend fun delete(bookmark: Bookmark) = bookmarksDao.delete(bookmark)

    suspend fun deleteAllBookmarks() = bookmarksDao.deleteAll()

    fun bookmarks() = Pager(PagingConfig(20)) { bookmarksDao.getBookmarks() }.flow

    fun searchBookmarks(query: String) = Pager(PagingConfig(20)) {
        bookmarksDao.searchBookmarks(query = query)
    }.flow

    /**
     * REMOTE
     */

    suspend fun createPublished(request: CreatePoemRequest) = poemsApi.createPoem(request)

    suspend fun updatePublished(request: EditPoemRequest) = poemsApi.updatePoem(request)

    suspend fun deletePublished(poemId: String) = poemsApi.deletePoem(PoemRequest(poemId))

    suspend fun getPublished(poemId: String) = poemsApi.getPoem(PoemRequest(poemId))

    suspend fun markAsRead(poemId: String) = poemsApi.markPoemAsRead(poemId)

    suspend fun bookmark(poemId: String) = poemsApi.bookmarkPoem(poemId = poemId)

    suspend fun unBookmark(poemId: String) = poemsApi.unBookmarkPoem(poemId = poemId)

    fun publishedPoems(query: String = "") = Pager(PagingConfig(20)) {
        PoemsMediator(query, poemsApi)
    }.flow

    fun getUserPoems(userId: String) = Pager(PagingConfig(20)) {
        UserPoemsMediator(userId, poemsApi)
    }.flow

}
