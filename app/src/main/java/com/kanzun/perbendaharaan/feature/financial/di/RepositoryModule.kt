package com.kanzun.perbendaharaan.feature.financial.di

import com.kanzun.perbendaharaan.feature.financial.data.repository.FinancialRepositoryImpl
import com.kanzun.perbendaharaan.feature.financial.domain.repository.FinancialRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFinancialRepository(
        impl: FinancialRepositoryImpl,
    ): FinancialRepository
}
