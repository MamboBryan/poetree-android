package com.mambo.poetree.data.local

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mambo.poetree.data.model.Emotion
import com.mambo.poetree.data.model.Poem
import com.mambo.poetree.data.model.Topic
import com.mambo.poetree.data.model.User
import com.mambo.poetree.di.ApplicationScope
import com.mambo.poetree.utils.EmotionsUtils
import com.mambo.poetree.utils.TopicUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(
    entities = [Poem::class, Emotion::class, Topic::class],
    exportSchema = false,
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun poemsDao(): PoemsDao
    abstract fun emotionsDao(): EmotionsDao
    abstract fun topicsDao(): TopicsDao

    companion object {
        const val DATABASE_NAME = "app_database"
    }

    class Callback @Inject constructor(
        private val database: Provider<AppDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            Log.i("APP_DATABASE", "onCreate: app database created")

            val poemDao = database.get().poemsDao()
            val emotionsDao = database.get().emotionsDao()
            val topicsDao = database.get().topicsDao()

            applicationScope.launch {

                val emotionUtils = EmotionsUtils()
                val topicUtils = TopicUtils()

                poemDao.insert(
                    Poem(
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
                emotionsDao.insertAll(emotionUtils.getEmotions())
                topicsDao.insertAll(topicUtils.getTopics())

            }
        }

    }
}