package com.mambo.poetree.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mambo.poetree.data.model.Emotion
import kotlinx.coroutines.flow.Flow

@Dao
interface EmotionsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(emotions: List<Emotion>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(emotion: Emotion)

    @Query("SELECT * FROM emotions")
    fun getAll(): Flow<List<Emotion>>

    @Query("SELECT * FROM emotions WHERE name LIKE '%' || :query || '%'")
    fun getEmotions(query: String): Flow<List<Emotion>>
}