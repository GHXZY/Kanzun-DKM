package com.kanzun.perbendaharaan.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kanzun.perbendaharaan.core.database.dao.AccountDao
import com.kanzun.perbendaharaan.core.database.dao.AssetDao
import com.kanzun.perbendaharaan.core.database.dao.AuditLogDao
import com.kanzun.perbendaharaan.core.database.dao.BudgetDao
import com.kanzun.perbendaharaan.core.database.dao.CategoryDao
import com.kanzun.perbendaharaan.core.database.dao.DonationDao
import com.kanzun.perbendaharaan.core.database.dao.FundDao
import com.kanzun.perbendaharaan.core.database.dao.FundraisingTargetDao
import com.kanzun.perbendaharaan.core.database.dao.MustahikDao
import com.kanzun.perbendaharaan.core.database.dao.TransactionDao
import com.kanzun.perbendaharaan.core.database.dao.TransferDao
import com.kanzun.perbendaharaan.core.database.dao.ZakatDao
import com.kanzun.perbendaharaan.core.database.entity.AccountEntity
import com.kanzun.perbendaharaan.core.database.entity.AssetCategoryEntity
import com.kanzun.perbendaharaan.core.database.entity.AssetEntity
import com.kanzun.perbendaharaan.core.database.entity.AuditLogEntity
import com.kanzun.perbendaharaan.core.database.entity.BackupMetadataEntity
import com.kanzun.perbendaharaan.core.database.entity.BudgetEntity
import com.kanzun.perbendaharaan.core.database.entity.BudgetItemEntity
import com.kanzun.perbendaharaan.core.database.entity.CategoryEntity
import com.kanzun.perbendaharaan.core.database.entity.DonationEntity
import com.kanzun.perbendaharaan.core.database.entity.FundEntity
import com.kanzun.perbendaharaan.core.database.entity.FundraisingTargetEntity
import com.kanzun.perbendaharaan.core.database.entity.MosqueEntity
import com.kanzun.perbendaharaan.core.database.entity.MosqueLetterheadEntity
import com.kanzun.perbendaharaan.core.database.entity.MustahikEntity
import com.kanzun.perbendaharaan.core.database.entity.PermissionEntity
import com.kanzun.perbendaharaan.core.database.entity.RoleEntity
import com.kanzun.perbendaharaan.core.database.entity.SignatureEntity
import com.kanzun.perbendaharaan.core.database.entity.TransactionAttachmentEntity
import com.kanzun.perbendaharaan.core.database.entity.TransactionEntity
import com.kanzun.perbendaharaan.core.database.entity.TransferEntity
import com.kanzun.perbendaharaan.core.database.entity.UserEntity
import com.kanzun.perbendaharaan.core.database.entity.ZakatTransactionEntity

import com.kanzun.perbendaharaan.core.database.dao.NotificationDao
import com.kanzun.perbendaharaan.core.database.entity.NotificationEntity

@Database(
    entities = [
        MosqueEntity::class,
        UserEntity::class,
        RoleEntity::class,
        PermissionEntity::class,
        AccountEntity::class,
        FundEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        TransactionAttachmentEntity::class,
        TransferEntity::class,
        AssetEntity::class,
        AssetCategoryEntity::class,
        ZakatTransactionEntity::class,
        MustahikEntity::class,
        FundraisingTargetEntity::class,
        DonationEntity::class,
        BudgetEntity::class,
        BudgetItemEntity::class,
        AuditLogEntity::class,
        SignatureEntity::class,
        MosqueLetterheadEntity::class,
        BackupMetadataEntity::class,
        NotificationEntity::class,
    ],
    version = 6,
    exportSchema = false,
)
@TypeConverters(DateConverters::class)
abstract class KanzunDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun transferDao(): TransferDao
    abstract fun fundDao(): FundDao
    abstract fun categoryDao(): CategoryDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun fundraisingTargetDao(): FundraisingTargetDao
    abstract fun assetDao(): AssetDao
    abstract fun zakatDao(): ZakatDao
    abstract fun mustahikDao(): MustahikDao
    abstract fun donationDao(): DonationDao
    abstract fun budgetDao(): BudgetDao
    abstract fun settingsDao(): com.kanzun.perbendaharaan.core.database.dao.SettingsDao
    abstract fun notificationDao(): NotificationDao
}

