package com.cellodove.bicyclesharing.di


import com.cellodove.domain.usecase.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@InstallIn(ViewModelComponent::class)
@Module
abstract class UseCaseModule {

    @ViewModelScoped
    @Binds
    abstract fun bindsFindRootUseCase(impl: FindRootUseCaseImpl): FindRootUseCase


    @ViewModelScoped
    @Binds
    abstract fun bindsBicyclesLocationUseCase(impl: BicyclesLocationUseCaseImpl): BicyclesLocationUseCase


    @ViewModelScoped
    @Binds
    abstract fun bindsSearchAddressUseCase(impl: SearchAddressUseCaseImpl): SearchAddressUseCase

}