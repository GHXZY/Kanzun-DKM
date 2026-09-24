package com.kanzun.perbendaharaan.feature.settings.di

import com.kanzun.perbendaharaan.feature.settings.data.repository.SettingsRepositoryImpl
import com.kanzun.perbendaharaan.feature.settings.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl,
    ): SettingsRepository
}
