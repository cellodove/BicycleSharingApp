package com.cellodove.bicyclesharing.di

import com.cellodove.MapRepositoryImpl
import com.cellodove.domain.repository.MapRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindsMapRepository(impl: MapRepositoryImpl): MapRepository

}