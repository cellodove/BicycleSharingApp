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
object RepositoryModule {

    @Singleton
    @Provides
    fun providesBookRepository(repository: MapRepositoryImpl): MapRepository {
        return repository
    }

}