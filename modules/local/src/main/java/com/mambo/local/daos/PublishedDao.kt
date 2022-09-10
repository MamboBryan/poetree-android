package com.mambo.local.daos

import androidx.paging.PagingSource
import androidx.room.*
import com.mambo.data.models.Poem
import com.mambo.data.models.Published
import kotlinx.coroutines.flow.Flow

@Dao
interface PublishedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(poems: List<Published>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(poem: Published): Long

    @Query("SELECT * FROM published_poems WHERE id = :id")
    fun get(id: Long): Poem

    @Query("SELECT * FROM published_poems")
    fun getAll(): PagingSource<Int, Poem>

    @Query("SELECT * FROM published_poems WHERE title LIKE '%' || :query || '%'")
    fun searchAll(query: String): PagingSource<Int, Poem>

    @Query("SELECT * FROM published_poems WHERE id = :id")
    fun getPoem(id: Int): Flow<Poem>

    @Delete
    suspend fun delete(poem: Published)

    @Query("DELETE  FROM published_poems")
    suspend fun deleteAll()

}