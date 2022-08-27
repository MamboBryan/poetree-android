package com.mambo.core.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.mambo.data.models.Topic
import com.mambo.data.responses.toTopicList
import com.mambo.local.TopicsDao
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
                val lastItem = state.lastItemOrNull() ?: return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                lastItem.id
            }
        }

        val response = poemsApi.getTopics(page = loadKey ?: 1)

        if (response.isSuccessful.not())
            return MediatorResult.Error(Exception(response.message))

        withContext(Dispatchers.IO) {
            if (loadType == LoadType.REFRESH) topicsDao.deleteAll()
            response.data?.list?.let { topicsDao.insertAll(it.toTopicList()) }
        }

        return MediatorResult.Success(endOfPaginationReached = response.data?.next == null)

    }

//    private suspend fun getCharacterKeysForFirstItem(state: PagingState<Int, Character>): CharacterKeys? {
//
//        val pagingSource = state.pages.firstOrNull()
//
//        val characters = pagingSource?.data
//
//        if (characters.isNullOrEmpty()) return null
//
//        val firstCharacter = characters.firstOrNull()
//
//        if (firstCharacter == null) return null
//
//        val keys = database.characterKeysDao().getCharacterKeysByCharacterId(firstCharacter.id!!)
//
//        Timber.i("Character Key for First Item => $keys")
//
//        return keys
//
////        ----------------------------------------------------------------------------------------------
////        09 -> A oneliner for all we've done above. THIS IS KOTLIN!!!
////        ----------------------------------------------------------------------------------------------
////        return state.pages
////            .lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
////            ?.let { article ->
////                database.articleKeysDao().getCharacterKeysByCharacterId(article.id)
////            }
//
//    }
//
//    private suspend fun getCharacterKeysClosestToCurrentPosition(state: PagingState<Int, Character>): CharacterKeys? {
//
//        val anchorPosition = state.anchorPosition
//
//        if (anchorPosition == null) return null
//
//        val closestItem = state.closestItemToPosition(anchorPosition)
//
//        if (closestItem == null) return null
//
//        val keys = database.characterKeysDao().getCharacterKeysByCharacterId(closestItem.id!!)
//
//        Timber.i("Character Key for Closest Item => $keys")
//
//        return keys
//
//    }
//
//    private suspend fun getCharacterKeysForLastItem(state: PagingState<Int, Character>): CharacterKeys? {
//
//        val pagingSource = state.pages.lastOrNull()
//
//        val characters = pagingSource?.data
//
//        if (characters.isNullOrEmpty()) return null
//
//        val lastCharacter = characters.lastOrNull()
//
//        if (lastCharacter == null) return null
//
//        val keys = database.characterKeysDao().getCharacterKeysByCharacterId(lastCharacter.id!!)
//
//        Timber.i("Character Key for Last Item => $keys")
//
//        return keys
//
////  ----------------------------------------------------------------------------------------------
////        10 -> A oneliner for all we've done above. NO WAYYYY!
////  ----------------------------------------------------------------------------------------------
////        return state.pages
////            .lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
////            ?.let { article ->
////                database.articleKeysDao().getCharacterKeysByCharacterId(article.id)
////            }
//
//    }

}