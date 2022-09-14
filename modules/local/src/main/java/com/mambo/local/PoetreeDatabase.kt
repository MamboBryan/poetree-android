package com.mambo.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mambo.data.models.*
import com.mambo.local.daos.*

@Database(
    entities = arrayOf(
        LocalPoem::class,
        Bookmark::class,
        Searched::class,
        Published::class,
        PublishedKeys::class,
        Topic::class
    ),
    exportSchema = false,
    version = 7
)
@TypeConverters(Converters::class)
abstract class PoetreeDatabase : RoomDatabase() {

    abstract fun poemsDao(): PoemsDao
    abstract fun topicsDao(): TopicsDao
    abstract fun searchedDao(): SearchedDao
    abstract fun bookmarksDao(): BookmarksDao
    abstract fun publishedDao(): PublishedDao
    abstract fun publishedKeysDao(): PublishedKeysDao

    companion object {
        const val DATABASE_NAME = "poetree_database"
    }

}