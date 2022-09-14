package com.mambo.local.daos

import androidx.room.*

@Entity(tableName = "published_keys")
data class PublishedKeys(
    @PrimaryKey
    val poemId: String,
    val prevKey: Int?,
    val nextKey: Int?
)

@Dao
interface PublishedKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<PublishedKeys>)

    @Query("SELECT * FROM published_keys WHERE poemId = :poemId")
    suspend fun getKeysByPoemId(poemId: String): PublishedKeys?

    @Query("DELETE FROM published_keys")
    suspend fun deleteAll()

}