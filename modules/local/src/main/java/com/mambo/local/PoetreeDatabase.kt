package com.mambo.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mambo.data.models.Poem
import com.mambo.data.models.Topic

@Database(
    entities = [Poem::class, Topic::class],
    exportSchema = false,
    version = 2
)
@TypeConverters(Converters::class)
abstract class PoetreeDatabase : RoomDatabase() {

    abstract fun poemsDao(): PoemsDao
    abstract fun topicsDao(): TopicsDao

    companion object {
        const val DATABASE_NAME = "poetree_database"
    }

}