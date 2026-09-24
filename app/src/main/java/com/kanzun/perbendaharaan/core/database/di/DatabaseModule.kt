package com.kanzun.perbendaharaan.core.database.di

import android.content.Context
import androidx.room.Room
import com.kanzun.perbendaharaan.core.database.KanzunDatabase
import com.kanzun.perbendaharaan.core.database.dao.AccountDao
import com.kanzun.perbendaharaan.core.database.dao.AssetDao
import com.kanzun.perbendaharaan.core.database.dao.AuditLogDao
import com.kanzun.perbendaharaan.core.database.dao.CategoryDao
import com.kanzun.perbendaharaan.core.database.dao.DonationDao
import com.kanzun.perbendaharaan.core.database.dao.FundDao
import com.kanzun.perbendaharaan.core.database.dao.FundraisingTargetDao
import com.kanzun.perbendaharaan.core.database.dao.MustahikDao
import com.kanzun.perbendaharaan.core.database.dao.TransactionDao
import com.kanzun.perbendaharaan.core.database.dao.TransferDao
import com.kanzun.perbendaharaan.core.database.dao.ZakatDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): KanzunDatabase {
        return Room.databaseBuilder(
            context,
            KanzunDatabase::class.java,
            "kanzun_perbendaharaan.db",
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideAccountDao(database: KanzunDatabase): AccountDao = database.accountDao()

    @Provides
    @Singleton
    fun provideTransactionDao(database: KanzunDatabase): TransactionDao = database.transactionDao()

    @Provides
    @Singleton
    fun provideTransferDao(database: KanzunDatabase): TransferDao = database.transferDao()

    @Provides
    @Singleton
    fun provideFundDao(database: KanzunDatabase): FundDao = database.fundDao()

    @Provides
    @Singleton
    fun provideCategoryDao(database: KanzunDatabase): CategoryDao = database.categoryDao()

    @Provides
    @Singleton
    fun provideAuditLogDao(database: KanzunDatabase): AuditLogDao = database.auditLogDao()

    @Provides
    @Singleton
    fun provideFundraisingTargetDao(database: KanzunDatabase): FundraisingTargetDao = database.fundraisingTargetDao()

    @Provides
    @Singleton
    fun provideAssetDao(database: KanzunDatabase): AssetDao = database.assetDao()

    @Provides
    @Singleton
    fun provideZakatDao(database: KanzunDatabase): ZakatDao = database.zakatDao()

    @Provides
    @Singleton
    fun provideMustahikDao(database: KanzunDatabase): MustahikDao = database.mustahikDao()

    @Provides
    @Singleton
    fun provideDonationDao(database: KanzunDatabase): DonationDao = database.donationDao()

    @Provides
    @Singleton
    fun provideBudgetDao(database: KanzunDatabase): com.kanzun.perbendaharaan.core.database.dao.BudgetDao = database.budgetDao()

    @Provides
    @Singleton
    fun provideSettingsDao(database: KanzunDatabase): com.kanzun.perbendaharaan.core.database.dao.SettingsDao = database.settingsDao()

    @Provides
    @Singleton
    fun provideNotificationDao(database: KanzunDatabase): com.kanzun.perbendaharaan.core.database.dao.NotificationDao = database.notificationDao()
}


