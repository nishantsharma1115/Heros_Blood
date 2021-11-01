package com.nishant.herosblood.di

import com.nishant.herosblood.repositories.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class InterfaceModule {

    @Binds
    abstract fun bindsAuthRepo(
        authRepository: AuthRepository
    ): AuthRepo

    @Binds
    abstract fun bindsDataRepo(
        dataRepository: DataRepository
    ): DataRepo

    @Binds
    abstract fun bindsLocationRepo(
        locationRepository: LocationRepository
    ): LocationRepo
}
