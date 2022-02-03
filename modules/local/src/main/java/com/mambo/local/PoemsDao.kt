package com.mambo.local

import androidx.paging.PagingSource
import androidx.room.*
import com.mambo.data.models.Poem
import kotlinx.coroutines.flow.Flow

@Dao
interface PoemsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(poems: List<Poem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg poem: Poem): Array<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert( poem: Poem): Long

    @Update
    suspend fun update(poem: Poem): Int

    @Delete
    suspend fun delete(poems: Poem)

    @Query("SELECT * FROM poems")
    fun getAllPoems(): PagingSource<Int, Poem>

    @Query("SELECT * FROM poems WHERE isBookmarked = 1 AND title LIKE '%' || :query || '%'")
    fun getBookmarks(query: String): PagingSource<Int, Poem>

    @Query("SELECT * FROM poems WHERE userId = :userId AND isPublic = 0 AND title LIKE '%' || :query || '%'")
    fun getUnPublishedPoems(userId: String, query: String): PagingSource<Int, Poem>

    @Query("SELECT * FROM poems")
    fun getAll(): Flow<List<Poem>>

    @Query("SELECT * FROM poems WHERE id = :id")
    fun getPoem(id: Int): Flow<Poem>

    @Query("SELECT * FROM poems WHERE id = :id")
    fun get(id: Long): Poem

    @Query("SELECT * FROM poems WHERE title LIKE '%' || :query || '%'")
    fun getPoems(query: String): PagingSource<Int, Poem>

    @Query("DELETE  FROM poems")
    suspend fun deleteAll()


}