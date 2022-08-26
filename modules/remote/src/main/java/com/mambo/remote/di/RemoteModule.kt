package com.mambo.remote.di

import android.content.Context
import com.mambo.data.preferences.UserPreferences
import com.mambo.remote.BuildConfig
import com.mambo.remote.interceptors.AuthInterceptor
import com.mambo.remote.interceptors.NetworkInterceptor
import com.mambo.remote.service.PoemsApi
import com.mambo.remote.service.PoemsClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() =
        HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun providesAuthInterceptor(preferences: UserPreferences) = AuthInterceptor(preferences)

    @Singleton
    @Provides
    fun providesNetworkInterceptor(@ApplicationContext context: Context) =
        NetworkInterceptor(context)

    @Singleton
    @Provides
    fun providesPoemsClient(
        authInterceptor: AuthInterceptor,
        networkInterceptor: NetworkInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ) = PoemsClient(
        authInterceptor = authInterceptor,
        networkInterceptor = networkInterceptor,
        loggingInterceptor = loggingInterceptor
    )

    @Singleton
    @Provides
    fun providesPoemsApi(
        preferences: UserPreferences,
        client: PoemsClient
    ) = PoemsApi(preferences, client)

}