package com.mambo.core.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.mambo.data.models.Poem
import com.mambo.data.models.Topic
import com.mambo.local.PoetreeDatabase
import com.mambo.remote.service.PoemsApi

@OptIn(ExperimentalPagingApi::class)
class SearchedPoemsMediator(
    private val topic: Topic? = null,
    private val query: String,
    private val database: PoetreeDatabase,
    private val poemsApi: PoemsApi,
) : RemoteMediator<Int, Poem>() {

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, Poem>
    ): MediatorResult {

        val loadKey = when (loadType) {
            LoadType.REFRESH -> null
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                lastItem.id
            }
        }

        val response = poemsApi.searchPoems(
            topicId = topic?.id,
            query = query,
            page = loadKey?.toInt() ?: 1
        )

        if (response.isSuccessful.not()) return MediatorResult.Error(Exception(response.message))

        database.withTransaction {
            val dao = database.searchedDao()
            if (loadType == LoadType.REFRESH) dao.deleteAll()
            response.data?.list?.forEach { dao.insert(it.toSearched()) }
        }

        return MediatorResult.Success(endOfPaginationReached = response.data?.next == null)

    }

}