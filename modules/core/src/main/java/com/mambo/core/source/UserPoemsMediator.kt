package com.mambo.core.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mambo.data.models.Poem
import com.mambo.remote.service.PoemsApi
import okio.IOException
import retrofit2.HttpException

class UserPoemsMediator(
    private val userId: String,
    private val poemsApi: PoemsApi,
) : PagingSource<Int, Poem>() {

    override fun getRefreshKey(state: PagingState<Int, Poem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Poem> {

        // Get the current page from the params
        val page = params.key ?: 1

        return try {
            // Get the response from the server
            val response = poemsApi.getUserPoems(userId = userId, page = page)

            val data = response.data ?: return LoadResult.Error(Exception(response.message))

            // Get the list of characters in the page
            val poems = data.list.map { it.toPoemDto() }

            // Get the next key for loading the next page
            val nextKey = data.next
            // val nextKey = if (articles.isEmpty()) null else page + 1

            // Get the previous key
            val previousKey = data.previous

            // Return the LoadResult with characters, previousKey and nextKey
            LoadResult.Page(data = poems, prevKey = previousKey, nextKey = nextKey)

        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }

    }


}