package com.mambo.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mambo.data.models.Bookmark
import com.mambo.data.models.LocalPoem
import com.mambo.data.models.Topic

@Database(
    entities = [LocalPoem::class, Bookmark::class, Topic::class],
    exportSchema = false,
    version = 4
)
@TypeConverters(Converters::class)
abstract class PoetreeDatabase : RoomDatabase() {

    abstract fun poemsDao(): PoemsDao
    abstract fun topicsDao(): TopicsDao
    abstract fun bookmarksDao(): BookmarksDao

    companion object {
        const val DATABASE_NAME = "poetree_database"
    }

}