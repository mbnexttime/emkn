package com.mcs.emkn.di

import com.mcs.emkn.network.Api
import com.squareup.moshi.Moshi
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import com.mcs.emkn.BuildConfig
import com.mcs.emkn.network.AuthResponseAdapterFactory
import com.mcs.emkn.network.MockApi
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkhttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .apply {
                readTimeout(60, TimeUnit.SECONDS)
                connectTimeout(60, TimeUnit.SECONDS)
            }
            .build()

    @Provides
    @Singleton
    fun provideApi(okHttpClient: OkHttpClient, moshi: Moshi): Api {
        if (BuildConfig.DEBUG) {
            return MockApi()
        }
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(AuthResponseAdapterFactory())
            .build()
            .create(Api::class.java)
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder().build()
}