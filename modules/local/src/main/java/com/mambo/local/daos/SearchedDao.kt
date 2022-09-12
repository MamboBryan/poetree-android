package com.mambo.local.daos

import androidx.paging.PagingSource
import androidx.room.*
import com.mambo.data.models.Poem
import com.mambo.data.models.Searched
import com.mambo.data.models.Topic

@Dao
interface SearchedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(poems: List<Searched>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg poem: Searched)

    @Query("SELECT * FROM searched_poems WHERE title LIKE '%' || :query || '%' or content LIKE '%' || :query || '%'")
    fun getAll(query: String): PagingSource<Int, Poem>

    @Query("SELECT * FROM searched_poems WHERE topic = :topic AND title LIKE '%' || :query || '%' or content LIKE '%' || :query || '%' ")
    fun getAll(topic: Topic, query: String): PagingSource<Int, Poem>

    @Delete
    suspend fun delete(poem: Searched)

    @Query("DELETE  FROM searched_poems")
    suspend fun deleteAll()

}