package com.mambo.poetree.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mambo.poetree.data.model.Topic
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(topics: List<Topic>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(topic: Topic)

    @Query("SELECT * FROM topics")
    fun getAll(): Flow<List<Topic>>

    @Query("SELECT * FROM topics WHERE name LIKE '%' || :query || '%'")
    fun getTopics(query: String): Flow<List<Topic>>

}