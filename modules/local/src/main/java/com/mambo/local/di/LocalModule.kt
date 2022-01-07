package com.mambo.local.di

import android.app.Application
import androidx.room.Room
import com.mambo.local.PoetreeDatabase
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
        callback: PoetreeDatabase.Callback
    ): PoetreeDatabase =
        Room.databaseBuilder(app, PoetreeDatabase::class.java, PoetreeDatabase.DATABASE_NAME)
            .addCallback(callback)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providePoemsDao(database: PoetreeDatabase) = database.poemsDao()

    @Provides
    fun provideTopicsDao(database: PoetreeDatabase) = database.topicsDao()

}