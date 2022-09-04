package com.mambo.core.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mambo.data.models.Comment
import com.mambo.data.models.Poem
import com.mambo.remote.service.PoemsApi
import okio.IOException
import retrofit2.HttpException

class CommentsMediator(
    private val poemId: String,
    private val poemsApi: PoemsApi,
) : PagingSource<Int, Comment>() {

    override fun getRefreshKey(state: PagingState<Int, Comment>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comment> {

        val page = params.key ?: 1

        return try {

            val response = poemsApi.getComments(poemId, page)

            val data = response.data ?: return LoadResult.Error(Exception(response.message))

            val comments = data.list.map { it.toComment() }

            val nextKey = data.next

            val previousKey = data.previous

            LoadResult.Page(data = comments, prevKey = previousKey, nextKey = nextKey)

        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }

    }


}