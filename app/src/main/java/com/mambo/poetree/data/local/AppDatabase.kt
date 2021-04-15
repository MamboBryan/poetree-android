package com.mambo.poetree.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mambo.poetree.data.model.Emotion
import com.mambo.poetree.data.model.Poem
import com.mambo.poetree.data.model.User
import com.mambo.poetree.di.ApplicationScope
import com.mambo.poetree.utils.EmotionsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Poem::class, Emotion::class], exportSchema = false, version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun poemsDao(): PoemsDao
    abstract fun emotionsDao(): EmotionsDao

    companion object {
        const val DATABASE_NAME = "app_database"
    }

    class Callback @Inject constructor(
        private val database: Provider<AppDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val poemDao = database.get().poemsDao()
            val emotionsDao = database.get().emotionsDao()

            applicationScope.launch {

                val emotionUtils = EmotionsUtils()

                poemDao.insert(
                    Poem(
                        title = "The Emergence",
                        content =
                        """
                            Hidden in the waves
                            Blossoming forth 
                            \n
                            The way the pen behaves
                            Like a cooking pot filled with broth
                            \n
                            The gem concealed in caves
                            Emerging slowly like a sloth
                            \n
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

                emotionsDao.insertAll(emotionUtils.getEmotions())

            }
        }

    }
}