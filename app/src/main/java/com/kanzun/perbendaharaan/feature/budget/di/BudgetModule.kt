package com.kanzun.perbendaharaan.feature.budget.di

import com.kanzun.perbendaharaan.feature.budget.data.repository.BudgetRepositoryImpl
import com.kanzun.perbendaharaan.feature.budget.domain.repository.BudgetRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BudgetModule {

    @Binds
    @Singleton
    abstract fun bindBudgetRepository(
        impl: BudgetRepositoryImpl,
    ): BudgetRepository
}
