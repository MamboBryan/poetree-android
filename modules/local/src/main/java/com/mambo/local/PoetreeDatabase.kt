package com.mambo.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mambo.data.models.Poem
import com.mambo.data.models.Topic
import com.mambo.data.models.User
import com.mambo.data.utils.getLocalPoem
import com.mambo.local.di.ApplicationScope
import com.mambo.data.utils.TopicUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(
    entities = [Poem::class, Topic::class],
    exportSchema = false,
    version = 1
)
@TypeConverters(Converters::class)
abstract class PoetreeDatabase : RoomDatabase() {

    abstract fun poemsDao(): PoemsDao
    abstract fun topicsDao(): TopicsDao

    companion object {
        const val DATABASE_NAME = "poetree_database"
    }

    class Callback @Inject constructor(
        private val database: Provider<PoetreeDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val poemDao = database.get().poemsDao()
            val topicsDao = database.get().topicsDao()

            applicationScope.launch {

                val topicUtils = TopicUtils()

                poemDao.insert(
                    getLocalPoem(
                        title = "The Emergence",
                        content =
                        """
                            Hidden in the waves
                            Blossoming forth 
                            
                            The way the pen behaves
                            Like a cooking pot filled with broth
                            
                            The gem concealed in caves
                            Emerging slowly like a sloth
                            
                            This is one of my faves
                            Uncover the veil and removes the cloth
                        """.trimIndent(),
                        user = User(
                            "poetree",
                            "Poetree",
                            "https://images.unsplash.com/photo-1558945657-484aa38065ec?ixlib=rb-1.2.1&ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&auto=format&fit=crop&w=633&q=80"
                        )

                    )
                )

                //prepopulate topics and emotions in DB
                topicsDao.insertAll(topicUtils.getTopics())

            }
        }

    }
}