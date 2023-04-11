package com.cellodove.bicyclesharing.di

import com.cellodove.data.source.FindRootDataSource
import com.cellodove.data.source.FindRootDataSourceImpl
import com.cellodove.data.source.SearchAddressDataSource
import com.cellodove.data.source.SearchAddressDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule{
    @Singleton
    @Provides
    fun providesFindRootDataSourceDataSource(source: FindRootDataSourceImpl): FindRootDataSource {
        return source
    }

    @Singleton
    @Provides
    fun providesSearchAddressDataSource(source: SearchAddressDataSourceImpl): SearchAddressDataSource {
        return source
    }
}