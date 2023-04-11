package com.cellodove.bicyclesharing.di

import com.cellodove.domain.repository.MapRepository
import com.cellodove.domain.usecase.FindRootUseCase
import com.cellodove.domain.usecase.SearchAddressUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class UseCaseModule {

    @Provides
    fun providesFindRootUseCase(repository: MapRepository) : FindRootUseCase {
        return FindRootUseCase(repository)
    }

    @Provides
    fun searchAddressUseCase(repository: MapRepository) : SearchAddressUseCase {
        return SearchAddressUseCase(repository)
    }
}