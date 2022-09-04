package com.mambo.local

import androidx.paging.PagingSource
import androidx.room.*
import com.mambo.data.models.Bookmark
import com.mambo.data.models.Poem

@Dao
interface BookmarksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(poems: List<Bookmark>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg poem: Bookmark): Array<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(poem: Bookmark)

    @Update
    suspend fun update(poem: Bookmark)

    @Delete
    suspend fun delete(poem: Bookmark)

    @Query("SELECT * FROM bookmarks")
    fun getBookmarks(): PagingSource<Int, Poem>

    @Query("SELECT * FROM bookmarks WHERE title LIKE '%' || :query || '%'")
    fun searchBookmarks(query: String): PagingSource<Int, Poem>

    @Query("DELETE  FROM poems")
    suspend fun deleteAll()

}