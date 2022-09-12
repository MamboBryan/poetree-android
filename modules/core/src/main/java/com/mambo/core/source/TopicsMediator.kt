package com.mambo.core.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.mambo.data.models.Topic
import com.mambo.data.responses.toTopicList
import com.mambo.local.daos.TopicsDao
import com.mambo.remote.service.PoemsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
class TopicsMediator(
    private val query: String,
    private val topicsDao: TopicsDao,
    private val poemsApi: PoemsApi,
) : RemoteMediator<Int, Topic>() {

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, Topic>
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

        val response = poemsApi.getTopics(page = loadKey ?: 1)

        if (response.isSuccessful.not())
            return MediatorResult.Error(Exception(response.message))

        withContext(Dispatchers.IO) {
            response.data?.list?.let { topicsDao.insertAll(it.toTopicList()) }
        }

        return MediatorResult.Success(endOfPaginationReached = response.data?.next == null)

    }

}