package com.mcs.emkn.di

import android.app.Application
import androidx.room.Room
import com.mcs.emkn.core.Router
import com.mcs.emkn.core.RouterImpl
import com.mcs.emkn.database.Database
import com.mcs.emkn.ui.utils.credentialsstorage.UiCredentialsStorage
import com.mcs.emkn.ui.utils.credentialsstorage.UiCredentialsStorageImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SingletonScopedModule {
    @Provides
    @Singleton
    fun provideRouter(
        routerImpl: RouterImpl
    ): Router {
        return routerImpl
    }

    @Provides
    @Singleton
    fun provideDb(
        application: Application,
    ): Database {
        return Room.databaseBuilder(
            application.applicationContext,
            Database::class.java, "emkn-database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideCredentialsStorage() : UiCredentialsStorage {
        return UiCredentialsStorageImpl()
    }
}