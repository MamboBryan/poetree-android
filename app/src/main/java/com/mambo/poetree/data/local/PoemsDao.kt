package com.mambo.poetree.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mambo.poetree.data.model.Poem
import kotlinx.coroutines.flow.Flow

@Dao
interface PoemsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(poems: List<Poem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(poems: Poem)

    @Update
    suspend fun update(poem: Poem)

    @Delete
    suspend fun delete(poems: Poem)

    @Query("SELECT * FROM poems")
    fun getAll(): Flow<List<Poem>>

    @Query("SELECT * FROM poems WHERE id = :id")
    fun getPoem(id: Int): Flow<Poem>

    @Query("SELECT * FROM poems WHERE title LIKE '%' || :query || '%'")
    fun getPoems(query: String): Flow<List<Poem>>

    @Query("DELETE  FROM poems")
    suspend fun deleteAll()


}