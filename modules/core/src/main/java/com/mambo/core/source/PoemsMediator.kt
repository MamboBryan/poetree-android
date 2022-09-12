package com.mambo.core.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.mambo.data.models.Poem
import com.mambo.data.utils.Constants.START_PAGE
import com.mambo.local.PoetreeDatabase
import com.mambo.local.daos.PublishedKeys
import com.mambo.remote.service.PoemsApi
import okio.IOException
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class PoemsMediator(
    private val poemsApi: PoemsApi,
    private val database: PoetreeDatabase,
) : RemoteMediator<Int, Poem>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Poem>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.PREPEND -> {
                val articleKeys = getKeysForFirstItem(state)
                val prevKey = articleKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = articleKeys != null)
                prevKey
            }
            LoadType.REFRESH -> {
                val articleKeys = getKeysClosestToCurrentPosition(state)
                articleKeys?.nextKey ?: START_PAGE
            }
            LoadType.APPEND -> {
                val articleKeys = getCharacterKeysForLastItem(state)
                val nextKey = articleKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = articleKeys != null)
                nextKey
            }
        }

        try {

            val response = poemsApi.getPoems(page = page)

            val data = response.data ?: return MediatorResult.Error(Exception(response.message))

            val poems = data.list

            val isEndOfPagination = data.next == null

            database.withTransaction {

                if (loadType == LoadType.REFRESH) {
                    database.publishedKeysDao().deleteAll()
                    database.publishedDao().deleteAll()
                }

                val prevKey = if (page == START_PAGE) null else page - 1
                val nextKey = if (isEndOfPagination) null else page + 1

                val keys = poems.map { PublishedKeys(it.id, prevKey, nextKey) }
                val published = poems.map { it.toPublished() }

                database.publishedKeysDao().insert(keys)
                database.publishedDao().insert(published)

            }

            return MediatorResult.Success(endOfPaginationReached = isEndOfPagination)

        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getKeysForFirstItem(state: PagingState<Int, Poem>): PublishedKeys? {

        val pagingSource = state.pages.firstOrNull()

        val data = pagingSource?.data

        if (data.isNullOrEmpty()) return null

        val firstItem = data.firstOrNull() ?: return null

        val keys = database.publishedKeysDao().getKeysByPoemId(firstItem.id)

        return keys

    }

    private suspend fun getKeysClosestToCurrentPosition(state: PagingState<Int, Poem>): PublishedKeys? {

        val anchorPosition = state.anchorPosition ?: return null

        val closestItem = state.closestItemToPosition(anchorPosition) ?: return null

        val keys = database.publishedKeysDao().getKeysByPoemId(closestItem.id)

        return keys

    }

    private suspend fun getCharacterKeysForLastItem(state: PagingState<Int, Poem>): PublishedKeys? {

        val pagingSource = state.pages.lastOrNull()

        val characters = pagingSource?.data

        if (characters.isNullOrEmpty()) return null

        val lastCharacter = characters.lastOrNull() ?: return null

        val keys = database.publishedKeysDao().getKeysByPoemId(lastCharacter.id)

        return keys

    }

}