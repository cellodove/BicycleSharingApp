package com.cellodove.bicyclesharing.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    private const val BASE_URL = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving"
    private const val CLIENT_ID = "gposaslhvm"
    private const val CLIENT_SECRET = "BKd62gpAk4yBkBMmrM1PL6NaeysYH6cWFupcuMmo"


    @Provides
    @Singleton
    fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(getHttpClient())
            .build()
    }

    @Provides
    @Singleton
    fun getHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            readTimeout(10, TimeUnit.SECONDS)
            connectTimeout(10, TimeUnit.SECONDS)
            writeTimeout(15, TimeUnit.SECONDS)
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }.addInterceptor { chain ->
            chain.proceed(
                chain.request()
                    .newBuilder()
                    .also {
                        it.addHeader("X-NCP-APIGW-API-KEY-ID",CLIENT_ID)
                        it.addHeader("X-NCP-APIGW-API-KEY",CLIENT_SECRET)
                    }
                    .build()
            )
        }
            .build()
    }
}