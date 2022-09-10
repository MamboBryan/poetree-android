package com.mambo.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mambo.data.models.Bookmark
import com.mambo.data.models.LocalPoem
import com.mambo.data.models.Published
import com.mambo.data.models.Topic
import com.mambo.local.daos.*

@Database(
    entities = [LocalPoem::class, Bookmark::class, Published::class, PublishedKeys::class, Topic::class],
    exportSchema = false,
    version = 6
)
@TypeConverters(Converters::class)
abstract class PoetreeDatabase : RoomDatabase() {

    abstract fun poemsDao(): PoemsDao
    abstract fun topicsDao(): TopicsDao
    abstract fun bookmarksDao(): BookmarksDao
    abstract fun publishedDao(): PublishedDao
    abstract fun publishedKeysDao(): PublishedKeysDao

    companion object {
        const val DATABASE_NAME = "poetree_database"
    }

}