package com.mambo.poetree.di

import android.app.Application
import androidx.room.Room
import com.mambo.poetree.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: AppDatabase.Callback
    ): AppDatabase =
        Room.databaseBuilder(app, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .addCallback(callback)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providePoemsDao(database: AppDatabase) = database.poemsDao()

    @Provides
    fun provideEmotionsDao(database: AppDatabase) = database.emotionsDao()

}